var winston = require('winston');
var async = require('async');
var _ = require('underscore');
var request = require('request');if (global.qsConfig && global.qsConfig.proxy) {request = request.defaults({'proxy' : global.qsConfig.proxy});}

var moment = require('moment');

var ItemSourceType = require('../goblin-common/ItemSourceType');
var GoblinError = require('../goblin-common/GoblinError');

var GoblinCrawler = require('./crawler/GoblinCrawler');

var GoblinSlave = module.exports;


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

GoblinSlave.start = function (config) {
    slaverModel = {
        config : config,
        running : true,
        timerId : {}
    };
    supportTypes.forEach(function (t) {
        _next(t);
    });
};

GoblinSlave.continue = function () {
    if (!slaverModel || !slaverModel.running) {
        return;
    }
    supportTypes.forEach(function (t) {
        if (!slaverModel.timerId[t]) {
            _next(t);
        }
    });
}

GoblinSlave.stop = function () {
    slaverModel.running = false;
    slaverModel = null;
};

var _next = function (type) {

    if (!slaverModel || !slaverModel.running) {
        return;
    }
    slaverModel.timerId[type] = null;

    async.waterfall([
        function (callback) {
            _queryNextItem(type, callback);
        }, function (item, callback) {
            GoblinCrawler.crawl(item.source, function (err, itemInfo) {
                callback(err, item, itemInfo);
            });
        }
    ], function (err, item, itemInfo) {
        _postItemInfo(item, itemInfo, err, function() {
            var delayTime = null;
            if (err) {
                //TODO handle error
                var failDelayConfig = slaverModel && slaverModel.config && slaverModel.config.failDelay || {};
                delayTime = _.random(failDelayConfig.min || 5000, failDelayConfig.max || 10000);
            } else {
                var succeedDelayConfig = slaverModel && slaverModel.config && slaverModel.config.succeedDelay || {};
                delayTime = _.random(succeedDelayConfig.min || 5000, succeedDelayConfig.max || 10000);
            }
            if (!slaverModel.timerId[type]) {
                slaverModel.timerId[type] = setTimeout(function () {
                    _next(type);
                }, delayTime);
            }

        });
    });
};

var _queryNextItem = function (type, callback) {
    var path = slaverModel.config.server.path + '/services/goblin/nextItem';

    // winston.debug('query nextItem: ' + type);
    request.post({
        url: path,
        form: {
            type : type,
            'version' : slaverModel.config.version
        }
    }, function(err, httpResponse, body){
        if (err) {
            // winston.debug('query nextItem failed: ' + type);
            callback(err);
        } else {
            var data = null;
            try {
                data = JSON.parse(body);;
            } catch(e) {}

            if (!data) {
                callback('Goblin Scheduler response empty data');
            } else if (data.metadata && data.metadata.error) {
                // winston.debug('query nextItem error: ' + type + ' > ' + data.metadata.error);
                callback(data.metadata.error);
            } else {
                var item = data && data.data && data.data.item;
                // winston.debug('query nextItem success: ' + type + ' > ' + item.source);
                callback(null, item);
            }
        }
    });
};

var _postItemInfo = function (item, itemInfo, err, callback) {
    if (!item) {
        callback();
        return;
    }

    var param = {};
    if (item && item._id) {
        param.itemId = item._id;
    }
    if (itemInfo) {
        param.itemInfo = itemInfo;
    }

    winston.info(moment().format('YYYY-MM-DD HH:mm:ss') + ' ' + item._id);
    winston.info('    ' + item.source);
    if (err) {
        if (err.errorCode === GoblinError.InvalidItemSource || err.errorCode === GoblinError.Delist) {
            winston.info('    delist: ' + err.errorCode);
        } else {
            param.error = err;
            winston.error('    err: ' + err);
        }
    } else {
        winston.info('    complete');
    }

    var path = slaverModel.config.server.path + '/services/goblin/crawlItemComplete';

    request.post({
        url: path,
        form: param
    }, function(err, httpResponse, body){
        callback();
    });
};