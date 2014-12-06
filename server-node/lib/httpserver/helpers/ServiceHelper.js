var async = require('async');

var MongoHelper = require('../helpers/MongoHelper');
var ContextHelper = require('../helpers/ContextHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var RequestHelper = require('../helpers/RequestHelper');

ServiceHelper = module.exports;

ServiceHelper.queryRelatedPeoples = function(req, res, RModel, queryField, resultField) {
    var pageNo, pageSize, numTotal;
    var _id;

    async.waterfall([
    function(callback) {
        try {
            var param = req.queryString;
            pageNo = parseInt(param.pageNo || 1), pageSize = parseInt(param.pageSize || 10);
            _id = RequestHelper.parseId(param._id);
            callback(null);
        } catch(err) {
            callback(ServerError.fromError(err));
        }
    },
    function(callback) {
        var criteria = {};
        criteria[queryField] = _id;
        MongoHelper.queryPaging(RModel.find(criteria).sort({
            'create' : -1
        }).populate(resultField), RModel.find(criteria), pageNo, pageSize, function(err, count, relationships) {
            numTotal = count;
            var peoples = [];
            if (!err) {
                relationships.forEach(function(relationship) {
                    if (relationship[resultField]) {
                        peoples.push(relationship[resultField]);
                    }
                });
            }
            callback(err, peoples);
        });
    },
    function(peoples, callback) {
        ContextHelper.appendPeopleContext(req.qsCurrentUserId, peoples, callback);
    }], function(err, peoples) {
        // Response
        ResponseHelper.responseAsPaging(res, err, {
            'peoples' : peoples
        }, pageSize, numTotal);
    });
};
