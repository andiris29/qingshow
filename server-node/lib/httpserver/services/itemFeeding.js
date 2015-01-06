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
        'postParseRequest' : aspectInceptions.postParseRequest,
        'postQuery' : function(qsParam, currentPageModels, numTotal, callback) {
            async.series([
            function(callback) {
                // Parse cover
                var tasks = [];
                currentPageModels.forEach(function(item) {
                    tasks.push(function(callback) {
                        item.updateCoverMetaData(function(err) {
                            callback(null, item);
                        });
                    });
                });
                async.parallel(tasks, callback);
            }], callback);
        },
        'preEndResponse' : aspectInceptions.preEndResponse
    });
};

var itemFeeding = module.exports;

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
            'postParseRequest' : function(raw) {
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
            'postParseRequest' : function(raw) {
                return {
                    '_id' : RequestHelper.parseId(raw._id)
                };
            }
        });
    }
};

