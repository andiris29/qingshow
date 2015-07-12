var mongoose = require('mongoose');
var async = require('async');
var _ = require('underscore');

//model
var Item = require('../../model/items');

//util
var RequestHelper = require('../helpers/RequestHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var MongoHelper = require('../helpers/MongoHelper');
var ServiceHelper = require('../helpers/ServiceHelper.js');

var ServerError = require('../server-error');

var item = module.exports;

item.find = {
    'method' : 'get',
    'func' : function(req, res) {
        ServiceHelper.queryPaging(req, res, function(qsParam, callback) {
            // querier
            var criteria = MongoHelper.querySchema(Item, req.queryString);
            MongoHelper.queryPaging(Item.find(criteria).sort({
                'create' : -1
            }), Item.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
        }, function(models) {
            // responseDataBuilder
            return {
                'items' : models
            };
        }, null);
    }
};




