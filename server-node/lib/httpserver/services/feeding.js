var mongoose = require('mongoose');
var async = require('async');
//model
var Show = require('../../model/shows');
var Brand = require('../../model/brands');
var Studio = require('../../model/studios');
var Item = require('../../model/items');
var People = require('../../model/peoples');
var Chosen = require('../../model/chosens');
var RPeopleLikeShow = require('../../model/rPeopleLikeShow');
//util
var RelationshipHelper = require('../helpers/RelationshipHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var RequestHelper = require('../helpers/RequestHelper');
var MongoHelper = require('../helpers/MongoHelper');

var ServerError = require('../server-error');

var _populate = function(shows, callback) {
    Show.populate(shows, 'modelRef', callback);
};

var _appendContext = function(shows, callback) {
    // TODO Append context
    callback(null, shows);
};
var _parseCover = function(shows, callback) {
    var tasks = [];
    shows.forEach(function(show) {
        tasks.push(function(callback) {
            show.updateCoverMetaData(function(err) {
                // Ignore the err
                if (err) {
                    console.log(new Date().toString() + ' - updateCoverMetaData failed');
                    console.log('\tshow: ' + show._id);
                    console.log('\tcover: ' + show.cover);
                    console.log('\terr: ' + err);
                }
                callback(null, show);
            });
        });
    });
    async.parallel(tasks, callback);
};
var _dataBuilder = function(err, shows) {
    return {
        'shows' : shows
    };
};
//feeding/recommendation
module.exports.recommendation = {
    'method' : 'get',
    'func' : function(req, res) {
        var pageNo, pageSize, numTotal;
        async.waterfall([
        function(callback) {
            try {
                var param = req.queryString;
                pageNo = parseInt(param.pageNo || 1), pageSize = parseInt(param.pageSize || 10);
                callback(null);
            } catch(err) {
                callback(ServerError.fromError(err));
            }
        },
        function(callback) {
            MongoHelper.queryPaging(Show.find().sort({
                'numLike' : -1
            }), Show.find(), pageNo, pageSize, function(err, count, shows) {
                numTotal = count;
                callback(err, shows);
            });
        }, _populate, _appendContext, _parseCover], function(err, shows) {
            // Response
            ResponseHelper.responseAsPaging(res, err, {
                'shows' : shows
            }, pageSize, numTotal);
        });
    }
};

module.exports.hot = {
    'method' : 'get',
    'func' : function(req, res) {
        var pageNo, pageSize, numTotal;
        async.waterfall([
        function(callback) {
            try {
                var param = req.queryString;
                pageNo = parseInt(param.pageNo || 1), pageSize = parseInt(param.pageSize || 10);
                callback(null);
            } catch(err) {
                callback(ServerError.fromError(err));
            }
        },
        function(callback) {
            MongoHelper.queryPaging(Show.find().sort({
                'numView' : -1
            }), Show.find(), pageNo, pageSize, function(err, count, shows) {
                numTotal = count;
                callback(err, shows);
            });
        }, _populate, _appendContext, _parseCover], function(err, shows) {
            // Response
            ResponseHelper.responseAsPaging(res, err, {
                'shows' : shows
            }, pageSize, numTotal);
        });
    }
};

module.exports.like = {
    'method' : 'get',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        var pageNo, pageSize, numTotal;
        async.waterfall([
        function(callback) {
            try {
                var param = req.queryString;
                pageNo = parseInt(param.pageNo || 1), pageSize = parseInt(param.pageSize || 10);
                callback(null);
            } catch(err) {
                callback(ServerError.fromError(err));
            }
        },
        function(callback) {
            var criteria = {
                'initiatorRef' : req.qsCurrentUserId
            };
            MongoHelper.queryPaging(RPeopleLikeShow.find(criteria).sort({
                'create' : -1
            }).populate('targetRef'), RPeopleLikeShow.find(criteria), pageNo, pageSize, function(err, count, relationships) {
                numTotal = count;
                callback(err, relationships);
            });
        },
        function(relationships, callback) {
            var shows = [];
            relationships.forEach(function(relationship) {
                shows.push(relationship.targetRef);
            });
            callback(null, shows);
        }, _populate, _appendContext, _parseCover], function(err, shows) {
            // Response
            ResponseHelper.responseAsPaging(res, err, {
                'shows' : shows
            }, pageSize, numTotal);
        });
    }
};

module.exports.chosen = {
    'method' : 'get',
    'func' : function(req, res) {
        var pageNo, pageSize, numTotal;
        var type, chosen;
        async.waterfall([
        function(callback) {
            try {
                var param = req.queryString;
                pageNo = parseInt(param.pageNo || 1), pageSize = parseInt(param.pageSize || 10);
                type = parseInt(param.type || 0);
                callback(null);
            } catch(err) {
                callback(ServerError.fromError(err));
            }
        },
        function(callback) {
            // Query chosen
            Chosen.find({
                'type' : type
            }).where('activateTime').lte(Date.now()).sort({
                'activateTime' : 1
            }).limit(1).exec(function(err, chosens) {
                if (err) {
                    callback(ServerError.fromDescription(err));
                } else if (!chosens || !chosens.length) {
                    callback(ServerError.fromCode(ServerError.ShowNotExist));
                } else {
                    chosen = chosens[0];
                    numTotal = chosen.showRefs.length;
                    callback(null);
                }
            });
        },
        function(callback) {
            // Query shows
            Chosen.populate(chosen, {
                'path' : 'showRefs',
                'options' : {
                    'skip' : (pageNo - 1) * pageSize,
                    'limit' : pageSize
                }
            }, function(err, chosen) {
                callback(err, chosen.showRefs);
            });
        }, _populate, _appendContext, _parseCover], function(err, shows) {
            // Response
            ResponseHelper.responseAsPaging(res, err, {
                'shows' : shows
            }, pageSize, numTotal, function(json) {
                json.metadata.refreshTime = chosen.activateTime;
                return json;
            });
        });
    }
};

module.exports.byModel = {
    'method' : 'get',
    'func' : function(req, res) {
        var pageNo, pageSize, numTotal;
        var modelRef;
        async.waterfall([
        function(callback) {
            try {
                var param = req.queryString;
                pageNo = parseInt(param.pageNo || 1), pageSize = parseInt(param.pageSize || 10);
                modelRef = mongoose.mongo.BSONPure.ObjectID(param._id);
                callback(null);
            } catch(err) {
                callback(ServerError.fromError(err));
            }
        },
        function(callback) {
            var criteria = {
                'modelRef' : modelRef
            };
            MongoHelper.queryPaging(Show.find(criteria).sort({
                'create' : -1
            }), Show.find(criteria), pageNo, pageSize, function(err, count, shows) {
                numTotal = count;
                callback(err, shows);
            });
        }, _populate, _appendContext, _parseCover], function(err, shows) {
            // Response
            ResponseHelper.responseAsPaging(res, err, {
                'shows' : shows
            }, pageSize, numTotal);
        });
    }
};

module.exports.byBrand = {
    'method' : 'get',
    'func' : function(req, res) {
        var pageNo, pageSize, numTotal;
        var brandRef;
        async.waterfall([
        function(callback) {
            try {
                var param = req.queryString;
                pageNo = parseInt(param.pageNo || 1), pageSize = parseInt(param.pageSize || 10);
                brandRef = mongoose.mongo.BSONPure.ObjectID(param._id);
                callback(null);
            } catch(err) {
                callback(ServerError.fromError(err));
            }
        },
        function(callback) {
            var criteria = {
                'brandRef' : brandRef
            };
            MongoHelper.queryPaging(Show.find(criteria).sort({
                'create' : -1
            }), Show.find(criteria), pageNo, pageSize, function(err, count, shows) {
                numTotal = count;
                callback(err, shows);
            });
        }, _populate, _appendContext, _parseCover], function(err, shows) {
            // Response
            ResponseHelper.responseAsPaging(res, err, {
                'shows' : shows
            }, pageSize, numTotal);
        });
    }
};

module.exports.byBrandDiscount = {
    'method' : 'get',
    'func' : function(req, res) {
        var pageNo, pageSize, numTotal;
        var brandRef;
        async.waterfall([
        function(callback) {
            try {
                var param = req.queryString;
                pageNo = parseInt(param.pageNo || 1), pageSize = parseInt(param.pageSize || 10);
                brandRef = mongoose.mongo.BSONPure.ObjectID(param._id);
                callback(null);
            } catch(err) {
                callback(ServerError.fromError(err));
            }
        },
        function(callback) {
            var criteria = {
                'brandRef' : brandRef,
                'brandDiscountInfo.start' : {
                    '$lte' : new Date()
                },
                'brandDiscountInfo.end' : {
                    '$gte' : new Date()
                }
            };
            MongoHelper.queryPaging(Show.find(criteria).sort({
                'brandDiscountInfo.end' : 1
            }), Show.find(criteria), pageNo, pageSize, function(err, count, shows) {
                numTotal = count;
                callback(err, shows);
            });
        }, _populate, _appendContext, _parseCover], function(err, shows) {
            // Response
            ResponseHelper.responseAsPaging(res, err, {
                'shows' : shows
            }, pageSize, numTotal);
        });
    }
};

module.exports.studio = {
    'method' : 'get',
    'func' : function(req, res) {
        var pageNo, pageSize, numTotal;
        async.waterfall([
        function(callback) {
            try {
                var param = req.queryString;
                pageNo = parseInt(param.pageNo || 1), pageSize = parseInt(param.pageSize || 10);
                callback(null);
            } catch(err) {
                callback(ServerError.fromError(err));
            }
        },
        function(callback) {
            var criteria = {
                'studioRef' : {
                    '$ne' : null
                }
            };
            MongoHelper.queryPaging(Show.find(criteria).sort({
                'create' : -1
            }), Show.find(criteria), pageNo, pageSize, function(err, count, shows) {
                numTotal = count;
                callback(err, shows);
            });
        }, _populate, _appendContext, _parseCover], function(err, shows) {
            // Response
            ResponseHelper.responseAsPaging(res, err, {
                'shows' : shows
            }, pageSize, numTotal);
        });
    }
};

module.exports.byStyles = {
    'method' : 'get',
    'func' : function(req, res) {
        var pageNo, pageSize, numTotal;
        var styles;
        async.waterfall([
        function(callback) {
            try {
                var param = req.queryString;
                pageNo = parseInt(param.pageNo || 1), pageSize = parseInt(param.pageSize || 10);
                styles = req.queryString.styles.split(',');
                callback(null);
            } catch(err) {
                callback(ServerError.fromError(err));
            }
        },
        function(callback) {
            var criteria = {
                '$or' : []
            };
            styles.forEach(function(style) {
                criteria['$or'].push({
                    'styles' : style
                });
            });
            MongoHelper.queryPaging(Show.find(criteria).sort({
                'create' : -1
            }), Show.find(criteria), pageNo, pageSize, function(err, count, shows) {
                numTotal = count;
                callback(err, shows);
            });
        }, _populate, _appendContext, _parseCover], function(err, shows) {
            // Response
            ResponseHelper.responseAsPaging(res, err, {
                'shows' : shows
            }, pageSize, numTotal);
        });
    }
};
