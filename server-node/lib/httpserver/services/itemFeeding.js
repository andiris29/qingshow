var mongoose = require('mongoose');
var async = require('async');
var _ = require('underscore');
//model
var Item = require('../../model/items');
//util
var RequestHelper = require('../helpers/RequestHelper');
var MongoHelper = require('../helpers/MongoHelper');

var ServerError = require('../server-error');

var _feed = function(req, res, querier, aspectInceptions) {
    aspectInceptions = aspectInceptions || {};
    ServiceHelper.queryPaging(req, res, querier, function(models) {
        // responseDataBuilder
        return {
            'items' : models
        };
    }, {
        'afterParseRequest' : aspectInceptions.afterParseRequest,
        'afterQuery' : function(qsParam, currentPageModels, numTotal, callback) {
            async.series([
            function(callback) {
                MongoHelper.updateCoverMetaData(currentPageModels, callback);
            }], callback);
        },
        'beforeEndResponse' : aspectInceptions.beforeEndResponse
    });
};

var itemFeeding = module.exports;

itemFeeding.random = {
    'method' : 'get',
    'func' : function(req, res) {
        _feed(req, res, function(qsParam, callback) {
            MongoHelper.queryRandom(Item.find(), Item.find(), qsParam.pageSize, function(err, models) {
                callback(err, models, 20);
            });
        }, {
            'beforeEndResponse' : function(json) {
                json.metadata.refreshTime = new Date();
                return json;
            }
        });
    }
};

itemFeeding.byBrandNew = {
    'method' : 'get',
    'func' : function(req, res) {
        _feed(req, res, function(qsParam, callback) {
            var criteria = {
                'brandRef' : qsParam._id,
                'brandNewInfo' : {
                    '$ne' : null
                }
            };
            MongoHelper.queryPaging(Item.find(criteria).sort({
                'brandNewInfo.order' : 1
            }), Item.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
        }, {
            'afterParseRequest' : function(raw) {
                return {
                    '_id' : RequestHelper.parseId(raw._id)
                };
            }
        });
    }
};

itemFeeding.byBrandDiscount = {
    'method' : 'get',
    'func' : function(req, res) {
        _feed(req, res, function(qsParam, callback) {
            var criteria = {
                'brandRef' : qsParam._id,
                'brandDiscountInfo' : {
                    '$ne' : null
                }
            };
            MongoHelper.queryPaging(Item.find(criteria).sort({
                'brandDiscountInfo.order' : 1
            }), Item.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
        }, {
            'afterParseRequest' : function(raw) {
                return {
                    '_id' : RequestHelper.parseId(raw._id)
                };
            }
        });
    }
};

