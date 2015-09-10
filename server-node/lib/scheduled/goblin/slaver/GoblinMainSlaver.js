var async = require('async');
var _ = require('underscore');

var GoblinScheduler = require('../scheduler/GoblinScheduler');
var ItemSyncService = require('../common/ItemSyncService');
var ItemSourceType = require('../common/ItemSourceType');
var GoblinError = require('../common/GoblinError');

var GoblinMainSlaver = module.exports;

//淘宝与天猫暂时分为一类
var supportTypes = [
    ItemSourceType.Taobao | ItemSourceType.Tmall,
    ItemSourceType.Hm,
    ItemSourceType.Jamy
];

/**
 *
 * @type {{config, running}}
 */
var slaverModel = null;

GoblinMainSlaver.start = function (config) {
    slaverModel = {
        config : config,
        running : true
    };
    supportTypes.forEach(function (t) {
        _next(t);
    });
};

GoblinMainSlaver.stop = function () {
    slaverModel.running = false;
    slaverModel = null;
};

var succeedDelay = [5000, 10000];
var failDelay = [5000, 10000];

var _next = function (type) {
    if (!slaverModel || !slaverModel.running) {
        return;
    }
    async.waterfall([
        function (callback) {
            GoblinScheduler.nextItem(type, callback);
        }, function (item, callback) {
            ItemSyncService.syncItem(item, callback);
        }
    ], function (err, item) {
        var innerCallback = function () {
            var delayTime = null;
            if (err && err.domain === GoblinError.Domain && err.errorCode === GoblinError.NoItemShouldBeCrawl) {
                delayTime = _.random(failDelay[0], failDelay[1]);
            } else {
                delayTime = _.random(succeedDelay[0], succeedDelay[1]);
            }
            setTimeout(function () {
                _next(type);
            }, delayTime);
        };

        if (item) {
            GoblinScheduler.finishItem(item && item._id, err, function (err, item) {
                innerCallback();
            });
        } else {
            innerCallback();
        }
    });
};

//TODO 外部主动触发slaver请求并爬取下一个item
GoblinMainSlaver.tregger = function () {

};