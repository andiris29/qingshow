
var async = require('async');

var RequestHelper = require('../helpers/RequestHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var GoblinScheduler = require('../../scheduled/goblin/scheduler/GoblinScheduler');
var ItemSyncService = require('../../scheduled/goblin/common/ItemSyncService');
var GoblinError = require('../../scheduled/goblin/common/GoblinError');
var Item = require('../../model/items');
var ServerError = require('../server-error');


var goblin = module.exports;

goblin.nextItem = {
    method : 'post',
    func : function (req, res) {
        var param = req.body;
        var type;
        if (param.type) {
            type = parseInt(param.type);
        }
        async.waterfall([
            function (callback) {
                GoblinScheduler.nextItem(type, callback);
            }
        ], function (err, item) {
            if (err && err.domain === GoblinError.Domain && err.errorCode === GoblinError.NoItemShouldBeCrawl) {
                //TODO refactor
                err = ServerError.fromCode(ServerError.GoblinError);
            }
            ResponseHelper.response(res, err, {
                item : item
            });
        });
    }
};

goblin.crawlItemComplete = {
    method : 'post',
    func : function (req, res) {
        var param = req.body;
        var itemIdStr = param.itemId;
        var itemInfo = param.itemInfo;
        var error = error;

        async.waterfall([
            function (callback) {
                Item.findOne({
                    _id : RequestHelper.parseId(itemIdStr)
                }, callback);
            }, function (item, callback) {
                if (!item) {
                    callback(ServerError.fromCode(ServerError.ItemNotExist));
                } else {
                    callback(null, item);
                }
            }, function (item, callback) {
                ItemSyncService.syncItemInfo(item, itemInfo, error, callback);
            }
        ], function (err, item) {
            ResponseHelper.response(res, err, {
                item : item
            });
        });

    }
};