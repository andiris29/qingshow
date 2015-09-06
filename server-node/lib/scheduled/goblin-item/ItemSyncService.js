var winston = require('winston');
var TaobaoWebItem = require('./taobao/Item');
var HmWebItem = require('./hm/Item');
var JamyWebItem = require('./jamy/Item');
var async = require('async');
var URLParser = require('./URLParser');
var _ = require('underscore');
var Item = require('../../model/items');

var ItemSyncService = {};

/**
 *
 * @param item
 * @param callback function(err, item)
 */
ItemSyncService.syncItem = function (item, callback) {
    if (!item) {
        callback(null, item);
        return;
    }
    var crawlCallback = function (err) {
        if (err) {
            item.delist = new Date();
            item.save(function (innerErr) {
                setTimeout(function () {
                    callback(innerErr || err, item);
                }, _.random(5000, 10000));
            });
        } else {
            setTimeout(function () {
                callback(err, item);
            }, _.random(5000, 10000));
        }
    };

    _logItem('item start', item);
    if (URLParser.isFromTaobao(item.source) || URLParser.isFromTmall(item.source)) {
        _crawlItemTaobaoInfo(item, crawlCallback);
    } else if (URLParser.isFromHm(item.source)) {
        _crawlItemHmInfo(item, crawlCallback);
    } else if (URLParser.isFromJamy(item.source)) {
        _crawlItemJamyInfo(item, crawlCallback);
    }

};

ItemSyncService.syncItemWithItemId = function (itemId, callback) {
    async.waterfall([
        function (callback) {
            Item.findOne({
                "_id" : itemId
            }, callback);
        }, function (item, callback) {
            ItemSyncService.syncItem(item, callback);
        }
    ], callback);
};


ItemSyncService.canParseItemSource = function (itemSouceStr) {
    if (!itemSouceStr) {
        return false;
    }
    return URLParser.isFromTaobao(itemSouceStr) ||
        URLParser.isFromTmall(itemSouceStr) ||
        URLParser.isFromHm(itemSouceStr) ||
        URLParser.isFromJamy(itemSouceStr);
};



var _crawlItemTaobaoInfo = function (item, callback) {
    async.waterfall([
        function (callback) {
            TaobaoWebItem.getSkus(item.source, function (err, taobaoInfo) {
                if (!taobaoInfo) {
                    callback('invalidSource');
                } else {
                    callback(err, taobaoInfo);
                }
            });
        }
    ], function (err, taobaoInfo) {
        if (err) {
            _logItem('item error', item);
            callback(err);
        } else {
            var delist = false;
            if (!taobaoInfo || !Object.keys(taobaoInfo).length) {
                callback('delist');
                _logItem('item failed', item);
            } else {
                item.delist = null;
                item.price = taobaoInfo.price;
                item.promoPrice = taobaoInfo.promo_price;
                item.skuProperties = taobaoInfo.skuProperties;
                item.sync = new Date();
                _logItem('item success', item);
                item.save(callback);
            }

        }
    });
};

var _crawlItemHmInfo = function (item, callback) {
    async.waterfall([function (callback) {
        HmWebItem.getSkus(item.source, function(err, hmInfo) {
            callback(err, hmInfo);
        });
    }], function (err, hmInfo) {
        if (err) {
            _logItem('item error', item);
            callback(err);
        } else {
            if (!hmInfo || !Object.keys(hmInfo).length) {
                _logItem('item failed', item);
                callback('delist');
            } else {
                item.delist = null;
                item.price = hmInfo.price;
                item.promoPrice = hmInfo.promo_price;
                item.skuProperties = hmInfo.skuProperties;
                item.sync = new Date();
                _logItem('item success', item);
                item.save(callback);
            }

        }
    });
};

var _crawlItemJamyInfo = function (item, callback) {
    async.waterfall([function (callback) {
        JamyWebItem.getSkus(item.source, function (err, jamyInfo) {
            callback(err, jamyInfo);
        });
    }], function (err, jamyInfo) {
        if (err) {
            _logItem('item error', item);
            callback(err);
        } else {
            if (!jamyInfo || !Object.keys(jamyInfo).length) {
                _logItem('item failed', item);
                callback('delist');
            } else {
                item.delist = null;
                item.price = jamyInfo.price;
                item.promoPrice = jamyInfo.promo_price;
                item.skuProperties = jamyInfo.skuProperties;
                item.sync = new Date();
                _logItem('item success', item);
                item.save(callback);
            }
        }
    });
};

var _logItem = function (content, item) {
    winston.info('[ItemSyncService] ' + content + ': ' + item._id);
};

module.exports = ItemSyncService;
