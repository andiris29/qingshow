var async = require('async'), _ = require('underscore');

var MongoHelper = require('../helpers/MongoHelper');
var ContextHelper = require('../helpers/ContextHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var RequestHelper = require('../helpers/RequestHelper');

ServiceHelper = module.exports;

// Invoke template
/*
 ServiceHelper.queryPaging(req, res, function(qsParam, callback) {
 // querier
 callback(err, models, numTotal);
 }, function(models) {
 // responseDataBuilder
 return {
 };
 }, {
 'postParseRequest' : function(raw) {
 },
 'postQuery' : function(qsParam, currentPageModels, numTotal, callback) {
 },
 'preEndResponse' : function(json) {
 return json;
 }
 });
 */
ServiceHelper.queryPaging = function(req, res, querier, responseDataBuilder, aspectInceptions) {
    // postQuery, postParseRequest, preSendResponse
    aspectInceptions = aspectInceptions || {};
    var qsParam;
    async.waterfall([
    function(callback) {
        // Parse request
        try {
            qsParam = RequestHelper.parsePageInfo(req.queryString);
            if (aspectInceptions.postParseRequest) {
                _.extend(qsParam, aspectInceptions.postParseRequest(req.queryString));
            }
            callback();
        } catch(err) {
            callback(ServerError.fromError(err));
        }
    },
    function(callback) {
        // Query
        querier(qsParam, function(err, currentPageModels, numTotal) {
            if (err) {
                callback(err);
            } else {
                if (currentPageModels.length === 0) {
                    callback(ServerError.PagingNotExist);
                } else {
                    if (aspectInceptions.postQuery) {
                        aspectInceptions.postQuery(qsParam, currentPageModels, numTotal, function(err) {
                            callback(err, currentPageModels, numTotal);
                        });
                    } else {
                        callback(err, currentPageModels, numTotal);
                    }
                }
            }
        });
    }], function(err, currentPageModels, numTotal) {
        // Send response
        var data, metadata;
        if (!err) {
            data = responseDataBuilder(currentPageModels);
            metadata = {
                'numTotal' : numTotal,
                'numPages' : parseInt((numTotal + qsParam.pageSize - 1) / qsParam.pageSize)
            };
        }
        ResponseHelper.response(res, err, data, metadata, aspectInceptions.preEndResponse);
    });
};

ServiceHelper.queryRelatedPeoples = function(req, res, RModel, queryField, resultField) {
    ServiceHelper.queryPaging(req, res, function(qsParam, callback) {
        var criteria = {};
        criteria[queryField] = qsParam._id;
        MongoHelper.queryPaging(RModel.find(criteria).sort({
            'create' : -1
        }).populate(resultField), RModel.find(criteria), qsParam.pageNo, qsParam.pageSize, function(err, relationships, count) {
            var peoples = [];
            if (!err) {
                relationships.forEach(function(relationship) {
                    if (relationship[resultField]) {
                        peoples.push(relationship[resultField]);
                    }
                });
            }
            callback(err, peoples, count);
        });
    }, function(peoples) {
        return {
            'peoples' : peoples
        };
    }, {
        'postParseRequest' : function(raw) {
            return {
                '_id' : RequestHelper.parseId(req.queryString._id)
            };
        },
        'postQuery' : function(qsParam, peoples, numTotal, callback) {
            ContextHelper.appendPeopleContext(req.qsCurrentUserId, peoples, callback);
        }
    });
};
