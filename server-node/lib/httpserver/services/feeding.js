var mongoose = require('mongoose');
var async = require('async'), _ = require('underscore');
//model
var Show = require('../../model/shows');
var RPeopleLikeShow = require('../../model/rPeopleLikeShow');
//util
var RequestHelper = require('../helpers/RequestHelper');
var MongoHelper = require('../helpers/MongoHelper');
var ContextHelper = require('../helpers/ContextHelper');

var ServerError = require('../server-error');

var _feed = function(req, res, querier, aspectInceptions) {
    aspectInceptions = aspectInceptions || {};
    ServiceHelper.queryPaging(req, res, querier, function(models) {
        // responseDataBuilder
        return {
            'shows' : models
        };
    }, {
        'afterParseRequest' : aspectInceptions.afterParseRequest,
        'afterQuery' : function(qsParam, currentPageModels, numTotal, callback) {
            async.series([
            function(callback) {
                // Populate
                _.delay(function() {
                    Show.populate(currentPageModels.filter(function(show) {
                        if (show.cover && req.session && req.session.assetsRoot) {
                            show.cover = req.session.assetsRoot + show.cover;
                        }
                        return !!show;
                    }), 'modelRef', callback);
                }, (res.qsPerformance && res.qsPerformance.d) ? res.qsPerformance.d : 1);
            },
            function(callback) {
                MongoHelper.updateCoverMetaData(currentPageModels, callback);
            },
            function(callback) {
                // Append context
                ContextHelper.appendShowContext(req.qsCurrentUserId, currentPageModels, callback);
            }], callback);
        },
        'beforeEndResponse' : aspectInceptions.beforeEndResponse
    });
};

var feeding = module.exports;

feeding.recommendation = {
    'method' : 'get',
    'func' : function(req, res) {
        _feed(req, res, function(qsParam, callback) {
            MongoHelper.queryPaging(Show.find().sort({
                // TODO
            }), Show.find().limit(20), qsParam.pageNo, qsParam.pageSize, callback);

        });
    }
};

feeding.hot = {
    'method' : 'get',
    'func' : function(req, res) {
        _feed(req, res, function(qsParam, callback) {
            MongoHelper.queryPaging(Show.find().sort({
                'numLike' : -1
            }), Show.find(), qsParam.pageNo, qsParam.pageSize, callback);
        });
    }
};

feeding.like = {
    'method' : 'get',
    'func' : function(req, res) {
        _feed(req, res, function(qsParam, callback) {
            async.waterfall([
            function(callback) {
                var criteria = {
                    'initiatorRef' : qsParam._id || req.qsCurrentUserId
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
        }, {
            'afterParseRequest' : function(raw) {
                return {
                    '_id' : RequestHelper.parseId(raw._id)
                };
            }
        });
    }
};
