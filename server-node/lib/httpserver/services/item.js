var mongoose = require('mongoose');
var async = require('async');
var _ = require('underscore');

//model
var Item = require('../../model/items');

//util
var RequestHelper = require('../helpers/RequestHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var MongoHelper = require('../helpers/MongoHelper');

var ServerError = require('../server-error');

var item = module.exports;

item.find = {
    'method' : 'get',
    'func' : function(req, res) {
        MongoHelper.querySchema(Item, req.queryString, function(err, items) {
            ResponseHelper.response(res, err, {
                'items' : items
            });
        });
    }
};




