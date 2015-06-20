var mongoose = require('mongoose');
var async = require('async');

// model
var Category = require('../../model/categories');
var Item = require('../../model/items');

// util
var ResponseHelper = require('../helpers/ResponseHelper');
var RequestHelper = require('../helpers/RequestHelper');
var ServiceHelper = require('../helpers/ServiceHelper');

var ServerError = require('../server-error');

var matcher = module.exports;

matcher.queryCategoy = {
    'method' : 'get',
    'func' : function(req, res) {
        Category.find({}).exec(function(err, categories) {
            ResponseHelper.response(res, err, {
                'categories' : categories
            });
        });
    }
};

matcher.queryItems = {
    'method' : 'get',
    'func' : function(req, res) {
        var qsParam = req.body;

        if (!qsParam.category || !qsParam.category.length) {
            ResponseHelper.response(res, ServerError.NotEnoughParam);
            return;
        }
        ServiceHelper.queryPaging(req, res, function(qsParam, callback) {
            var id = RequestHelper.parseId(qsParam.category);
            var criteria = {
                'categoryRef' : id
            }
            MongoHelper.queryPaging(Item.find(criteria), Item.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
        }, function(items) {
            // responseDataBuilder
            return {
                'items' : items 
            };
        }, {});
    }
};



