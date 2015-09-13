var async = require('async');
var _ = require('underscore');
var request = require('request');


var ItemSourceType = require('../common/ItemSourceType');
var GoblinError = require('../common/GoblinError');

var GoblinCrawler = require('../common/crawler/GoblinCrawler');

var GoblinMainSlaver = module.exports;


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

var _next = function (type) {
    if (!slaverModel || !slaverModel.running) {
        return;
    }
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
            setTimeout(function () {
                _next(type);
            }, delayTime);
        });
    });
};

var _queryNextItem = function (type, callback) {
    var path = slaverModel.config.server.path + '/services/goblin/nextItem';

    request.post({
        url: path,
        form: {
            type : type
        }
    }, function(err, httpResponse, body){
        if (err) {
            callback(err);
        } else {
            var data = JSON.parse(body);;

            if (data.metadata && data.metadata.error) {
                callback(data.metadata.error);
            } else {
                var item = data && data.data && data.data.item;
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

    if (err) {
        param.error = err;
    }

    var path = slaverModel.config.server.path + '/services/goblin/crawlItemComplete';

    request.post({
        url: path,
        form: param
    }, function(err, httpResponse, body){
        console.log(item._id + ':' + item.source );
        callback();
    });
};