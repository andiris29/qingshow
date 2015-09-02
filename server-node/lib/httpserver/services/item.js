var mongoose = require('mongoose');
var async = require('async');
var _ = require('underscore');

var Items = require('../../model/items');

var RequestHelper = require('../helpers/RequestHelper');
var ResponseHelper = require('../helpers/ResponseHelper');

var ServerError = require('../server-error');

var item = module.exports;

item.updateExpectablePrice = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        var param = req.body;
        async.waterfall([function(callback) {
            Items.findOne({
                _id : RequestHelper.parseId(param._id)
            }, function(error, item) {
                if (error) {
                    callback(error);
                } else if (!item) {
                    callback(ServerError.ItemNotExist);
                } else {
                    callback(null, item);
                }
            });
        }, function(item, callback) {
            item.expectablePrice = RequestHelper.parseNumber(param.expectablePrice);
            item.save(function(error, item) {
                callback(error, item);
            });
        }], function(error, item) {
            ResponseHelper.response(res, error, {
                item : item
            });
        });
    }
};

item.removeExpectablePrice = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        var param = req.body;
        async.waterfall([function(callback) {
            Items.collection({
                _id : RequestHelper.parseId(param._id)
            }, {
                $unset : { expectablePrice : 0 }
            }, {
            }, function(error) {
                callback(error);
            });
        }, function(callback) {
            Items.findOne({
                _id : RequestHelper.parseId(param._id)
            }, function(error, item) {
                callback(error, item);
            });
        }], function(error, item) {
            ResponseHelper.response(res, error, {
                item : item
            });
        });
    }
};

