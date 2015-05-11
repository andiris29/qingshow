var mongoose = require('mongoose');
var async = require('async'), _ = require('underscore');
//model
var Show = require('../../model/shows');
var Peoples = require('../../model/peoples');
var RPeopleLikeShow = require('../../model/rPeopleLikeShow');
//util
var RequestHelper = require('../helpers/RequestHelper');
var MongoHelper = require('../helpers/MongoHelper');
var ContextHelper = require('../helpers/ContextHelper');

var ServerError = require('../server-error');

var _feed = function (req, res, querier, aspectInceptions) {
    aspectInceptions = aspectInceptions || {};
    ServiceHelper.queryPaging(req, res, querier, function (models) {
        // responseDataBuilder
        return {
            'shows' : models
        };
    }, {
        'afterParseRequest' : aspectInceptions.afterParseRequest,
        'afterQuery' : function (qsParam, currentPageModels, numTotal, callback) {
            async.series([
                function (callback) {
                    // Populate
                    _.delay(function () {
                        Show.populate(currentPageModels.filter(function(show) {
                            if (show.cover && req.session && req.session.assetsRoot) {
                                show.cover = req.session.assetsRoot + show.cover;
                            }
                            return !!show;
                        }), 'modelRef', callback);
                    }, (res.qsPerformance && res.qsPerformance.d) ? res.qsPerformance.d : 1);
                },
                function (callback) {
                    MongoHelper.updateCoverMetaData(currentPageModels, callback);
                },
                function (callback) {
                    // Append context
                    ContextHelper.appendShowContext(req.qsCurrentUserId, currentPageModels, callback);
                },
                function (callback) {
                    if (aspectInceptions.afterQuery) {
                        aspectInceptions.afterQuery(qsParam, currentPageModels, numTotal, callback);
                    } else {
                        callback();
                    }
                }], callback);
        },
        'beforeEndResponse' : aspectInceptions.beforeEndResponse
    });
};

var feeding = module.exports;

feeding.recommendation = {
    'method' : 'get',
    'func' : function (req, res) {
        _feed(req, res, function (qsParam, outCallback) {
            async.waterfall([
                function (callback) {
                    callback();
                    var userid = req.qsCurrentUserId;
//                    Peoples.findOne({'_id' : userid}, callback);
                }, function (people, callback) {

//                    var rate = people.weight / people.height;
                    var rate = 0.27;
                    var type = null;
                    /*
                     * 0.24~0.27属于偏瘦型（A1）
                     0.28~0.31属于标准型（A2）
                     0.32~0.40属于偏胖型（A3）
                     0.41~0.50属于超胖型（A4）
                     * */
                    if (rate < 0.275) {
                        type = 'A1';
                    } else if (rate < 0.315) {
                        type = 'A2';
                    } else if (rate < 0.405) {
                        type = 'A3';
                    } else {
                        type = 'A4';
                    }
                    var criteria = {
                        'recommend.group' : type
                    };
                    MongoHelper.queryPaging(Show.find(criteria).sort({
                        'recommend.date' : 1
                    }), Show.find(criteria), qsParam.pageNo, qsParam.pageSize, outCallback);
                }
            ], outCallback);
        }, {
            afterQuery : function (qsParam, currentPageModels, numTotal, callback) {
                Show.populate(currentPageModels, {
                    'path' : 'itemRefs',
                    'model' : "items"
                }, callback);
            }
        });
    }
};

feeding.hot = {
    'method' : 'get',
    'func' : function (req, res) {
        _feed(req, res, function (qsParam, callback) {
            MongoHelper.queryPaging(Show.find().sort({
                'numLike' : -1
            }), Show.find(), qsParam.pageNo, qsParam.pageSize, callback);
        });
    }
};

feeding.like = {
    'method' : 'get',
    'func' : function (req, res) {
        _feed(req, res, function (qsParam, callback) {
            async.waterfall([
                function (callback) {
                    var criteria = {
                        'initiatorRef' : qsParam._id || req.qsCurrentUserId
                    };
                    MongoHelper.queryPaging(RPeopleLikeShow.find(criteria).sort({
                        'create' : -1
                    }).populate('targetRef'), RPeopleLikeShow.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
                },
                function (relationships, count, callback) {
                    var shows = [];
                    relationships.forEach(function (relationship) {
                        shows.push(relationship.targetRef);
                    });
                    callback(null, shows, count);
                }], callback);
        }, {
            'afterParseRequest' : function (raw) {
                return {
                    '_id' : RequestHelper.parseId(raw._id)
                };
            }
        });
    }
};
