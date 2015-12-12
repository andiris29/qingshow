var mongoose = require('mongoose');
var async = require('async'), _ = require('underscore');
//model
var Show = require('../../dbmodels').Show;
var ShowCode = require('../../dbmodels').ShowCode;
var Peoples = require('../../dbmodels').People;
var RPeopleLikeShow = require('../../dbmodels').RPeopleLikeShow;
//util
var RequestHelper = require('../../helpers/RequestHelper');
var MongoHelper = require('../../helpers/MongoHelper');
var ContextHelper = require('../../helpers/ContextHelper');
var ServiceHelper = require('../../helpers/ServiceHelper');

var errors = require('../../errors');

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
                function(callback) {
                    // Populate ownerRef
                    Show.populate(currentPageModels, {
                        'path' : 'ownerRef',
                        'model' : 'peoples'
                    }, callback);
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
                    var userid = req.qsCurrentUserId;
                    Peoples.findOne({'_id' : userid}, callback);
                }, function (people, callback) {
                    if (!people) {
                        callback(errors.PeopleNotExist);
                        return;
                    }
                    var rate = 1;
                    if (people.weight && people.height) {
                        rate = people.weight / people.height;
                    }
                    var type = null;
                    /*
                     * 0.24~0.27属于偏瘦型（A1）
                     * 0.28~0.31属于标准型（A2）
                     * 0.32~0.40属于偏胖型（A3）
                     * 0.41~0.50属于超胖型（A4）
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
                        '$and' : [
                            { 'recommend.group' : type} 
                        ]
                    };
                    MongoHelper.queryPaging(Show.find(criteria).sort({
                        'recommend.date' : -1
                    }), Show.find(criteria), qsParam.pageNo, qsParam.pageSize, outCallback);
                }
            ], outCallback);
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

feeding.time = {
    'method' : 'get',
    'func' : function(req, res) {
        _feed(req, res, function(qsParam, outCallback) {
            async.waterfall([
            function(callback) {
                var criteria = _buildFeaturedCriteria(req);
                MongoHelper.queryPaging(Show.find(criteria).sort({
                    'create' : -1
                }), Show.find(criteria), qsParam.pageNo, qsParam.pageSize, outCallback);
            }], outCallback);
        });
    }
};

feeding.matchCreatedBy = {
    'method' : 'get',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        _feed(req, res, function(qsParam, callback) {
            var _id = req.qsCurrentUserId;
            if (qsParam._id !== null && qsParam._id.length > 0) {
                _id = RequestHelper.parseId(qsParam._id);
            }
            var criteria = {
                '$and' : [{
                    'ownerRef' : _id
                }, {
                    '$or' : [{
                        'hideAgainstOwner' : false 
                    } , {
                        'hideAgainstOwner' : {
                            '$exists' : false
                        }
                    }]
                }]
            };
            
            MongoHelper.queryPaging(Show.find(criteria).sort({
                'create' : -1
            }), Show.find(criteria), qsParam.pageNo, qsParam.pageSize, function(err, shows, count) {
                callback(err, shows, count);
            });
        });
    }
};

feeding.hot = {
    'method': 'get',
    'func': function(req, res) {
        _feed(req, res, function(qsParam, callback) {
            var now = new Date();
            var yesterday = now;
            yesterday.setDate(now.getDate() - 1);
            var criteria = {
                create: {
                    '$gte': yesterday
                }
            };

            MongoHelper.queryPaging(Show.find(criteria).sort({
                numView: -1
            }), show.find(criteria), qsParam.pageNo, qsParam.pageSize, function(err, shows, count) {
                callback(err, shows, count);
            });
        });
    }
};

var _buildFeaturedCriteria = function(req) {
    var criteria = [
    ];
    if (req.queryString.from) {
        criteria.push({'create' : {'$gte' : RequestHelper.parseDate(req.queryString.from)}});
    }
    if (req.queryString.to) {
        criteria.push({'create' : {'$lt' : RequestHelper.parseDate(req.queryString.to)}});
    }
    return {
        '$and' : criteria
    };
};

