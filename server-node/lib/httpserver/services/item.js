/**
 * Created by wxy325 on 9/3/15.
 */
var RequestHelper = require('../helpers/RequestHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var async = require('async');
var item = module.exports;
var ItemSyncService = require("../../scheduled/goblin-item/ItemSyncService");
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

