
var async = require('async');

var RequestHelper = require('../../helpers/RequestHelper');
var ResponseHelper = require('../../helpers/ResponseHelper');
var GoblinScheduler = require('./goblin/GoblinScheduler');
var ItemSyncService = require('../../goblin-slave/ItemSyncService');
var GoblinError = require('../../goblin-slave/GoblinError');
var Item = require('../../dbmodels').Item;
var errors = require('../../errors');

var winston = require('winston');
var logger = require('../../runtime').loggers.get('goblin');

var goblin = module.exports;

goblin.nextItem = {
    method : 'post',
    func : function (req, res) {
        // TODO https://github.com/andiris29/com.focosee.qingshow/issues/1812
        ResponseHelper.response(res, errors.GoblinSlaveDisabled);
        return;
        
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
                err = errors.genGoblin('genGoblin', err);
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
                    callback(errors.ItemNotExist);
                } else {
                    callback(null, item);
                }
            }, function (item, callback) {
                ItemSyncService.syncItemInfo(item, itemInfo, error, callback);
            }, function (item, callback) {
                GoblinScheduler.finishItem(item._id, error, callback);
            }
        ], function (err, item) {
            if (!err) {
                logger.info({
                    'ip' : RequestHelper.getIp(req),
                    'nextItem' : item._id ? item._id.toString() : ''
                });
            }
            ResponseHelper.response(res, err, {
                item : item
            });
        });

    }
};

goblin.crawlItemFailed = {
    method : 'post',
    func : function (req, res) {
        var param = req.body;
        var log = param.log;
        logger.info('Slaver Exception:\n' + log);
        ResponseHelper.response(res, null, {});
    }
};

require('./goblin/GoblinScheduler').start(global.qsConfig.schedule.goblinScheduler);
require('./goblin/GoblinMainSlaver').start(global.qsConfig.schedule.goblinMainSlaver);
    