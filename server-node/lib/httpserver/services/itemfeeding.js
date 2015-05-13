var ServiceHelper = require('../helpers/ServiceHelper');
var MongoHelper = require('../helpers/MongoHelper');
var RPeopleLikeItem = require('../../model/rPeopleLikeItem');
var Items = require('../../model/items');
var async = require('async');
var RequestHelper = require('../helpers/RequestHelper');
var ContextHelper = require('../helpers/ContextHelper');
var itemfeeding = module.exports;

itemfeeding.like = {
    'method' : 'get',
    'func' : function (req, res) {
        ServiceHelper.queryPaging(req, res, function (qsParam, callback) {
            async.waterfall([
                function (callback) {
                    var criteria = {
                        'initiatorRef' : qsParam._id || req.qsCurrentUserId
                    };
                    MongoHelper.queryPaging(RPeopleLikeItem.find(criteria).sort({
                        'create' : -1
                    }).populate('targetRef'), RPeopleLikeItem.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
                },
                function (relationships, count, callback) {
                    var items = [];
                    relationships.forEach(function (relationship) {
                        items.push(relationship.targetRef);
                    });
                    callback(null, items, count);
                }, function (items, count, callback) {
                    Items.populate(items, 'brandRef', function () {
                        callback(null, items, count);
                    });
                }
            ], callback);
        }, function (models) {
            // responseDataBuilder
            return {
                'items' : models
            };
        }, {
            'afterQuery' : function (qsParam, currentPageModels, numTotal, callback) {
                //update cover metadata
                async.series([
                    function (callback) {
                        MongoHelper.updateCoverMetaData(currentPageModels, callback);
                    }, function (callback) {
                        ContextHelper.appendItemContext(req.qsCurrentUserId, currentPageModels, callback);
                    }], callback);
            },
            'afterParseRequest' : function(raw) {
                return {
                    '_id' : RequestHelper.parseId(raw._id)
                };
            }
        });
    }
};