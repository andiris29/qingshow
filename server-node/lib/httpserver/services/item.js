/**
 * Created by wxy325 on 9/3/15.
 */
var RequestHelper = require('../helpers/RequestHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var async = require('async');
var Items = require('../../model/Items');

var item = module.exports;
var ItemSyncService = require("../../scheduled/goblin/common/ItemSyncService");
var GoblinError = require("../../scheduled/goblin/common/GoblinError");
var ServerError = require('../server-error');

item.sync = {
    method : 'post',
    func : function (req, res) {
        async.waterfall([
            function (callback) {
                var itemId = RequestHelper.parseId(req.body._id);
                ItemSyncService.syncItemWithItemId(itemId, callback);
            }
        ], function (err, item) {
            if (err) {
                if (err.domain === GoblinError.Domain) {
                    err = new ServerError(ServerError.GoblinError, err.description, err);
                }
                ResponseHelper.response(res, err);
            } else if (!item) {
                ResponseHelper.response(res, ServerError.ItemNotExist);
            } else {
                ResponseHelper.response(res, null, {
                    'item' : item
                });
            }
        });
    }
};

item.delist = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        async.waterfall([function(callback) {
            Items.findOne({
                _id : RequestHelper.parseId(req.body._id)
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
            item.delist = Date.now();
            item.syncEnabled = false;
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

