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
var ShowChosen = require('../../model/showChosens');
var RPeopleLikeShow = require('../../model/rPeopleLikeShow');
//util
var RelationshipHelper = require('../helpers/RelationshipHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var RequestHelper = require('../helpers/RequestHelper');
var MongoHelper = require('../helpers/MongoHelper');
var ContextHelper = require('../helpers/ContextHelper');

var ServerError = require('../server-error');

var feeding = module.exports;

// var _dataBuilder = function(err, shows) {
// return {
// 'shows' : shows
// };
// };

var _feeding = function(req, res, querier, aspectInceptions) {
    aspectInceptions = aspectInceptions || {};
    ServiceHelper.queryPaging(req, res, querier, function(models) {
        // responseDataBuilder
        return {
            'shows' : models
        };
    }, {
        'postParseRequest' : aspectInceptions.postParseRequest,
        'postQuery' : function(qsParam, currentPageModels, numTotal, callback) {
            async.series([
            function(callback) {
                // Populate
                Show.populate(currentPageModels.filter(function(show) {
                    return !!show;
                }), 'modelRef', callback);
            },
            function(callback) {
                // Parse cover
                var tasks = [];
                currentPageModels.forEach(function(show) {
                    tasks.push(function(callback) {
                        show.updateCoverMetaData(function(err) {
                            callback(null, show);
                        });
                    });
                });
                async.parallel(tasks, callback);
            },
            function(callback) {
                // Append context
                ContextHelper.appendShowContext(req.qsCurrentUserId, currentPageModels, callback);
            }], callback);
        },
        'preEndResponse' : aspectInceptions.preEndResponse
    });
};
/**
 *
 * @param {Object} req
 * @param {Object} res
 * @param {Object} showFinder function(qsParam, callback){callback(null, count, shows);}
 * @param {Object} queryStringParser function(queryString){return {};}
 * @param {Object} beforeResponseEnd function(json){return {};}
 */

//
// var _feed = function(req, res, showFinder, queryStringParser, beforeResponseEnd) {
// var qsParam.pageNo, qsParam.pageSize, numTotal;
// async.waterfall([
// function(callback) {
// try {
// qsParam.pageNo = parseInt(req.queryString.qsParam.pageNo || 1);
// qsParam.pageSize = parseInt(req.queryString.qsParam.pageSize || 10);
// var qsParam = queryStringParser ? queryStringParser(req.queryString) : null;
// callback(null, qsParam);
// } catch(err) {
// callback(ServerError.fromError(err));
// }
// },
// function(qsParam, callback) {
// showFinder(qsParam.pageNo, qsParam.pageSize, qsParam, function(err, count, shows) {
// numTotal = count;
// if (!err && shows.length === 0) {
// err = ServerError.PagingNotExist;
// }
// callback(err, shows);
// });
// }, _populate, _parseCover,
// function(shows, callback) {
// ContextHelper.appendShowContext(req.qsCurrentUserId, shows, callback);
// }], function(err, shows) {
// // Response
// ResponseHelper.responseAsPaging(res, err, {
// 'shows' : shows
// }, qsParam.pageSize, numTotal, beforeResponseEnd);
// });
// };
//feeding/recommendation
feeding.recommendation = {
    'method' : 'get',
    'func' : function(req, res) {
        _feeding(req, res, function(qsParam, callback) {
            MongoHelper.queryPaging(Show.find().sort({
                'numView' : -1
            }), Show.find().limit(20), qsParam.pageNo, qsParam.pageSize, callback);
        });
    }
};

feeding.hot = {
    'method' : 'get',
    'func' : function(req, res) {
        _feeding(req, res, function(qsParam, callback) {
            MongoHelper.queryPaging(Show.find().sort({
                'numView' : -1
            }), Show.find(), qsParam.pageNo, qsParam.pageSize, callback);
        });
    }
};

feeding.like = {
    'method' : 'get',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        _feeding(req, res, function(qsParam, callback) {
            async.waterfall([
            function(callback) {
                var criteria = {
                    'initiatorRef' : req.qsCurrentUserId
                };
                MongoHelper.queryPaging(RPeopleLikeShow.find(criteria).sort({
                    'create' : -1
                }).populate('targetRef'), RPeopleLikeShow.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
            },
            function(relationships, count, callback) {
                var shows = [];
                relationships.forEach(function(relationship) {
                    shows.push(relationship.targetRef);
                });
                callback(null, shows, count);
            }], callback);
        });
    }
};

feeding.chosen = {
    'method' : 'get',
    'func' : function(req, res) {
        var chosen;
        _feeding(req, res, function(qsParam, callback) {
            async.waterfall([
            function(callback) {
                // Query chosen
                ShowChosen.find({
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
                var skip = (qsParam.pageNo - 1) * qsParam.pageSize;
                chosen = new ShowChosen({
                    'activateTime' : chosen.activateTime,
                    'showRefs' : chosen.showRefs.filter(function(show, index) {
                        return index >= skip && index < skip + qsParam.pageSize;
                    })
                });
                ShowChosen.populate(chosen, {
                    'path' : 'showRefs'
                }, function(err, chosen) {
                    callback(err, chosen.showRefs, count);
                });
            }], callback);
        }, {
            'postParseRequest' : function(raw) {
                return {
                    'type' : RequestHelper.parseNumber(raw.type) || 0
                };
            },
            'preEndResponse' : function(json) {
                if (chosen) {
                    json.metadata.refreshTime = chosen.activateTime;
                }
                return json;
            }
        });
    }
};

feeding.byModel = {
    'method' : 'get',
    'func' : function(req, res) {
        _feeding(req, res, function(qsParam, callback) {
            var criteria = {
                'modelRef' : qsParam._id
            };
            MongoHelper.queryPaging(Show.find(criteria).sort({
                'create' : -1
            }), Show.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
        }, {
            'postParseRequest' : function(raw) {
                return {
                    '_id' : RequestHelper.parseId(raw._id)
                };
            }
        });
    }
};

feeding.byBrandNew = {
    'method' : 'get',
    'func' : function(req, res) {
        _feeding(req, res, function(qsParam, callback) {
            var criteria = {
                'brandRef' : qsParam._id,
                'brandNewOrder' : {
                    '$ne' : null
                }
            };
            MongoHelper.queryPaging(Show.find(criteria).sort({
                'brandNewOrder' : 1
            }), Show.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
        }, {
            'postParseRequest' : function(raw) {
                return {
                    '_id' : RequestHelper.parseId(raw._id)
                };
            }
        });
    }
};

feeding.byBrandDiscount = {
    'method' : 'get',
    'func' : function(req, res) {
        _feeding(req, res, function(qsParam, callback) {
            var criteria = {
                'brandRef' : qsParam._id,
                'brandDiscountOrder' : {
                    '$ne' : null
                }
            };
            MongoHelper.queryPaging(Show.find(criteria).sort({
                'brandDiscountOrder' : 1
            }), Show.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
        }, {
            'postParseRequest' : function(raw) {
                return {
                    '_id' : RequestHelper.parseId(raw._id)
                };
            }
        });
    }
};

feeding.studio = {
    'method' : 'get',
    'func' : function(req, res) {
        _feeding(req, res, function(qsParam, callback) {
            var criteria = {
                'studioRef' : {
                    '$ne' : null
                }
            };
            MongoHelper.queryPaging(Show.find(criteria).sort({
                'create' : -1
            }), Show.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
        });
    }
};
