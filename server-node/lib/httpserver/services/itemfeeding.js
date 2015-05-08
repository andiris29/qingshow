var ServiceHelper = require('../helpers/ServiceHelper');
var MongoHelper = require('../helpers/MongoHelper');
var RPeopleLikeItem = require('../../model/rPeopleLikeItem');
var async = require('async');

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
                MongoHelper.updateCoverMetaData(currentPageModels, callback);
            }
        });
    }
};