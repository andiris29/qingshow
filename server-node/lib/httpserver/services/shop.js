var mongoose = require('mongoose');
var async = require('async');
//model
var Item = require('../../model/items');
var Trade = require('../../model/trades');

//util
var MongoHelper = require('../helpers/MongoHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var RequestHelper = require('../helpers/RequestHelper');
var ServiceHelper = require('../helpers/ServiceHelper');

var ServerError = require('../server-error');

var shop = module.exports;

shop.queryItems = {
    'method' : 'get',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        var param = req.body;
        ServiceHelper.queryPaging(req, res, function(param, callback) {
            var criteria = {
                shopRef : RequestHelper.parseId(param._id)
            };
            MongoHelper.queryPaging(Item.find(criteria), Item.find(criteria), param.pageSize, callback);
        }, function(items) {
            return {
                'items' : items
            };
        }, {});
    }
};

shop.queryTradesByItem = {
    'method' : 'get',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        var param = req.body;
        ServiceHelper.queryPaging(req, res, function(param, callback) {
            var criteria = {
                itemRef: RequestHelper.parseId(param._id)
            };
            MongoHelper.queryPaging(Trade.find(criteria), Trade.find(criteria), param.pageSize, callback);
        }, function(trades) {
            return {
                'trades' : trades
            };
        }, {});
    }
};

