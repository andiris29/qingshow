var mongoose = require('mongoose');
var async = require('async');

var RequestHelper = require('../../helpers/RequestHelper');
var ResponseHelper = require('../../helpers/ResponseHelper');
var MongoHelper = require('../../helpers/MongoHelper');

var errors = require('../../errors');

var admin = module.exports;

var _collectionToModel = {
    'items' : 'Item',
    'shows' : 'Show',
    'categories' : 'Category'
};

admin.find = {
    'method' : 'get',
    // 'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        var model = req.queryString.model || _collectionToModel[req.queryString.collection];
        var Model = require('../../models/' + model);
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
