var mongoose = require('mongoose');
var async = require('async');

var RequestHelper = require('../helpers/RequestHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var MongoHelper = require('../helpers/MongoHelper');

var ServerError = require('../server-error');

var admin = module.exports;

admin.find = {
    'method' : 'get',
    // 'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        var Model = require('../../models/' + req.queryString.model);
        ServiceHelper.queryPaging(req, res, function(qsParam, callback) {
            // querier
            var criteria = MongoHelper.querySchema(Model, req.queryString);
            MongoHelper.queryPaging(Model.find(criteria).sort({
                'create' : -1
            }), Model.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
        }, function(models) {
            return {
                'models' : models
            };
        }, null);
    }
};
