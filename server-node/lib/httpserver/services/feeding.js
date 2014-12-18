var mongoose = require('mongoose');
var async = require('async');
var _ = require('underscore');
//model
var Show = require('../../model/shows');
var ShowComments = require('../../model/showComments');
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
var ContextHelper = require('../helpers/ContextHelper');

var ServerError = require('../server-error');

feeding = module.exports;

var _populate = function(shows, callback) {
    var array = [];
    shows.forEach(function(show) {
        if (show) {
            array.push(show);
        }
    });
    Show.populate(array, 'modelRef', callback);
};

/**
 * Ignore error
 *
 * @param {Object} shows
 * @param {Object} callback
 */
var _parseCover = function(shows, callback) {
    var tasks = [];
    shows.forEach(function(show) {
        tasks.push(function(callback) {
            show.updateCoverMetaData(function(err) {
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

/**
 *
 * @param {Object} req
 * @param {Object} res
 * @param {Object} showFinder function(qsParam, callback){callback(null, count, shows);}
 * @param {Object} queryStringParser function(queryString){return {};}
 * @param {Object} beforeResponseEnd function(json){return {};}
 */

var _feed = function(req, res, showFinder, queryStringParser, beforeResponseEnd) {
    var pageNo, pageSize, numTotal;
    async.waterfall([
    function(callback) {
        try {
            pageNo = parseInt(req.queryString.pageNo || 1);
            pageSize = parseInt(req.queryString.pageSize || 10);
            var qsParam = queryStringParser ? queryStringParser(req.queryString) : null;
            callback(null, qsParam);
        } catch(err) {
            callback(ServerError.fromError(err));
        }
    },
    function(qsParam, callback) {
        showFinder(pageNo, pageSize, qsParam, function(err, count, shows) {
            numTotal = count;
            if (!err && shows.length === 0) {
                err = ServerError.PagingNotExist;
            }
            callback(err, shows);
        });
    }, _populate, _parseCover,
    function(shows, callback) {
        ContextHelper.appendShowContext(req.qsCurrentUserId, shows, callback);
    }], function(err, shows) {
        // Response
        ResponseHelper.responseAsPaging(res, err, {
            'shows' : shows
        }, pageSize, numTotal, beforeResponseEnd);
    });
};
//feeding/recommendation
feeding.recommendation = {
    'method' : 'get',
    'func' : function(req, res) {
        _feed(req, res, function(pageNo, pageSize, qsParam, callback) {
            MongoHelper.queryPaging(Show.find().sort({
                'numView' : -1
            }), Show.find(), pageNo, pageSize, callback);
        });
    }
};

feeding.hot = {
    'method' : 'get',
    'func' : function(req, res) {
        _feed(req, res, function(pageNo, pageSize, qsParam, callback) {
            MongoHelper.queryPaging(Show.find().sort({
                'numView' : -1
            }), Show.find(), pageNo, pageSize, callback);
        });
    }
};

feeding.like = {
    'method' : 'get',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        _feed(req, res, function(pageNo, pageSize, qsParam, callback) {
            async.waterfall([
            function(callback) {
                var criteria = {
                    'initiatorRef' : req.qsCurrentUserId
                };
                MongoHelper.queryPaging(RPeopleLikeShow.find(criteria).sort({
                    'create' : -1
                }).populate('targetRef'), RPeopleLikeShow.find(criteria), pageNo, pageSize, function(err, count, relationships) {
                    callback(err, count, relationships);
                });
            },
            function(count, relationships, callback) {
                var shows = [];
                relationships.forEach(function(relationship) {
                    shows.push(relationship.targetRef);
                });
                callback(null, count, shows);
            }], callback);
        });
    }
};

feeding.chosen = {
    'method' : 'get',
    'func' : function(req, res) {
        var chosen;
        _feed(req, res, function(pageNo, pageSize, qsParam, callback) {
            async.waterfall([
            function(callback) {
                // Query chosen
                Chosen.find({
                    'type' : qsParam.type
                }).where('activateTime').lte(Date.now()).sort({
                    'activateTime' : 1
                }).limit(1).exec(function(err, chosens) {
                    if (err) {
                        callback(ServerError.fromDescription(err));
                    } else if (!chosens || !chosens.length) {
                        callback(ServerError.fromCode(ServerError.ShowNotExist));
                    } else {
                        chosen = chosens[0];
                        callback(null, chosen.showRefs.length);
                    }
                });
            },
            function(count, callback) {
                // Query shows
                var skip = (pageNo - 1) * pageSize;
                chosen = new Chosen({
                    'activateTime' : chosen.activateTime,
                    'showRefs' : chosen.showRefs.filter(function(show, index) {
                        return index >= skip && index < skip + pageSize;
                    })
                });
                Chosen.populate(chosen, {
                    'path' : 'showRefs'
                }, function(err, chosen) {
                    callback(err, count, chosen.showRefs);
                });
            }], callback);
        }, function(queryString) {
            return {
                'type' : parseInt(queryString.type || 0)
            };
        }, function(json) {
            if (chosen) {
                json.metadata.refreshTime = chosen.activateTime;
            }
            return json;
        });
    }
};

feeding.byModel = {
    'method' : 'get',
    'func' : function(req, res) {
        _feed(req, res, function(pageNo, pageSize, qsParam, callback) {
            var criteria = {
                'modelRef' : qsParam.modelRef
            };
            MongoHelper.queryPaging(Show.find(criteria).sort({
                'create' : -1
            }), Show.find(criteria), pageNo, pageSize, callback);
        }, function(queryString) {
            return {
                'modelRef' : RequestHelper.parseId(queryString._id)
            };
        }, function(json) {
            return json;
        });
    }
};

feeding.byBrandNew = {
    'method' : 'get',
    'func' : function(req, res) {
        _feed(req, res, function(pageNo, pageSize, qsParam, callback) {
            var criteria = {
                'brandRef' : qsParam.brandRef,
                'brandNewOrder' : {
                    '$ne' : null
                }
            };
            MongoHelper.queryPaging(Show.find(criteria).sort({
                'brandNewOrder' : 1
            }), Show.find(criteria), pageNo, pageSize, callback);
        }, function(queryString) {
            return {
                'brandRef' : RequestHelper.parseId(queryString._id)
            };
        }, function(json) {
            return json;
        });
    }
};

feeding.byBrandDiscount = {
    'method' : 'get',
    'func' : function(req, res) {
        _feed(req, res, function(pageNo, pageSize, qsParam, callback) {
            var criteria = {
                'brandRef' : qsParam.brandRef,
                'brandDiscountOrder' : {
                    '$ne' : null
                }
            };
            MongoHelper.queryPaging(Show.find(criteria).sort({
                'brandDiscountOrder' : 1
            }), Show.find(criteria), pageNo, pageSize, callback);
        }, function(queryString) {
            return {
                'brandRef' : RequestHelper.parseId(queryString._id)
            };
        });
    }
};

feeding.studio = {
    'method' : 'get',
    'func' : function(req, res) {
        _feed(req, res, function(pageNo, pageSize, qsParam, callback) {
            var criteria = {
                'studioRef' : {
                    '$ne' : null
                }
            };
            MongoHelper.queryPaging(Show.find(criteria).sort({
                'create' : -1
            }), Show.find(criteria), pageNo, pageSize, callback);
        });
    }
};

feeding.byStyles = {
    'method' : 'get',
    'func' : function(req, res) {
        _feed(req, res, function(pageNo, pageSize, qsParam, callback) {
            var criteria = {
                '$or' : []
            };
            qsParam.styles.forEach(function(style) {
                criteria['$or'].push({
                    'styles' : style
                });
            });
            MongoHelper.queryPaging(Show.find(criteria).sort({
                'create' : -1
            }), Show.find(criteria), pageNo, pageSize, callback);
        }, function(queryString) {
            return {
                'styles' : RequestHelper.parseArray(queryString.styles)
            };
        });
    }
};
