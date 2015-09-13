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
        running : true,
        timer : {}  //用于记录某个爬虫当前是否正在进行
    };
    GoblinMainSlaver.triggerAllTimer();
};

GoblinMainSlaver.triggerAllTimer = function () {
    supportTypes.forEach(function (t) {
        if (!slaverModel.timer[t]) {
            _next(t);
        }
    });
};


GoblinMainSlaver.stop = function () {
    slaverModel.running = false;
    slaverModel = null;
};


var _next = function (type) {
    if (!slaverModel || !slaverModel.running) {
        return;
    }
    async.waterfall([
        function (callback) {
            GoblinScheduler.nextRequestedItem(type, callback);
        }, function (item, callback) {
            ItemSyncService.syncItem(item, callback);
        }
    ], function (err, item) {
        var innerCallback = function () {
            var delayTime = null;
            if (err && err.domain === GoblinError.Domain && err.errorCode === GoblinError.NoItemShouldBeCrawl) {
                var failDelayConfig = (slaverModel && slaverModel.config && slaverModel.config.failDelay) || {};
                var failDelayMax = failDelayConfig.max || 10000;
                var failDelayMin = failDelayConfig.min || 5000;
                delayTime = _.random(failDelayMin, failDelayMax);
            } else {
                var succeedDelayConfig = (slaverModel && slaverModel.config && slaverModel.config.succeedDelay) || {};
                var succeedDelayMax = succeedDelayConfig.max || 10000;
                var succeedDelayMin = succeedDelayConfig.min || 5000;
                delayTime = _.random(succeedDelayMin, succeedDelayMax);
            }
            if (err && err.domain === GoblinError.Domain && err.errorCode === GoblinError.NoItemShouldBeCrawl) {
                slaverModel.timer[type] = null;
            } else {
                slaverModel.timer[type] = setTimeout(function () {
                    _next(type);
                }, delayTime);
            }
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

GoblinMainSlaver.trigger = function (item) {
    GoblinMainSlaver.triggerAllTimer();
};