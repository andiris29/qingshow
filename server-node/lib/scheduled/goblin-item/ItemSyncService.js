var winston = require('winston');
var TaobaoWebItem = require('./taobao/Item');
var HmWebItem = require('./hm/Item');
var JamyWebItem = require('./jamy/Item');
var async = require('async');
var URLParser = require('./URLParser');
var _ = require('underscore');
var Item = require('../../model/items');
var People = require('../../model/peoples');
var ServerError = require('../../httpserver/server-error');

var ItemSyncService = {};

var crypto = require('crypto'), _secret = 'qingshow@secret';

ItemSyncService.isOutDate = function (item) {
    if (!item || !item.sync) {
        return true;
    }
    var now = new Date();
    // 暂定为 1小时 需要重新sync
    return ((now - item.sync) > 1000 * 60 * 60 * 1);
};
/**
 *
 * @param item
 * @param callback function(err, item, log)
 */
ItemSyncService.syncItem = function (item, callback) {
    if (!item || !ItemSyncService.isOutDate(item)) {
        callback(null, item);
        return;
    }

    var crawlCallback = function (err, i, count, log) {
        if (err) {
            item.sync = new Date();
            item.delist = new Date();
            item.save(function (innerErr) {
                callback(innerErr || err, item, log);
            });
        } else {
            callback(err, item, log);
        }
    };

    _logItem('item start', item);
    if (URLParser.isFromTaobao(item.source) || URLParser.isFromTmall(item.source)) {
        _crawlItemTaobaoInfo(item, crawlCallback);
    } else if (URLParser.isFromHm(item.source)) {
        _crawlItemHmInfo(item, crawlCallback);
    } else if (URLParser.isFromJamy(item.source)) {
        _crawlItemJamyInfo(item, crawlCallback);
    } else {
        callback(ServerError.fromCode(ServerError.NotSupportItemSource), item);
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


/**]
 *
 * @param item
 * @param callback
 *            function (err, item, count, log)
 *                                          option.delist  boolean
 * @private
 */

var _crawlItemTaobaoInfo = function (item, callback) {
    async.waterfall([
        function (callback) {
            TaobaoWebItem.getSkus(item.source, function (err, taobaoInfo) {
                if (!taobaoInfo) {
                    callback(ServerError.fromCode(ServerError.InvalidItemSource));
                } else {
                    callback(err, taobaoInfo);
                }
            });
        }, function (taobaoInfo, callback) {
            var delist = false;
            if (!taobaoInfo || !Object.keys(taobaoInfo).length) {
                _logItem('item success, delist', item);
                callback(null, item, 0, 'delist');

            } else {
                item.delist = null;
                item.price = taobaoInfo.price;
                item.promoPrice = taobaoInfo.promo_price;
                item.skuProperties = taobaoInfo.skuProperties;
                item.sync = new Date();
            }
            callback(null, taobaoInfo);
        }, function (taobaoInfo, callback) {
            //taobaoInfo.shopId
            var shopInfo = taobaoInfo.shopInfo;
            if (shopInfo && shopInfo.shopId) {
                var shopId = shopInfo.shopId;
                async.waterfall([
                    function (callback) {
                        People.findOne({
                            'shopInfo.taobao.sid' : shopInfo.shopId
                        }, callback);
                    }, function (people, callback) {
                        if (!people) {
                            new People({
                                nickname: shopInfo.shopName,
                                userInfo : {
                                    id : shopId,
                                    encryptedPassword : _encrypt(shopId)
                                }
                            }).save(callback);
                        } else {
                            callback(null, people);
                        }
                    }, function (people, number, callback) {
                        item.shopRef = people._id;
                        item.save(callback);
                    }], callback);
            } else {
                _logItem('item success', item);
                item.save(callback);
            }
        }
    ], function (err, item) {
        if (err) {
            _logItem('item error', item);
            callback(err);
        } else {

            callback(null, item);
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
                _logItem('item success, delist', item);
                callback(null, item, 0, 'delist');
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
                _logItem('item success, delist', item);
                callback(null, item, 0, 'delist');
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


//TODO add accountService, remove _encrypt
var _encrypt = function(string) {
    var cipher = crypto.createCipher('aes192', _secret);
    var enc = cipher.update(string, 'utf8', 'hex');
    enc += cipher.final('hex');
    return enc;
};

module.exports = ItemSyncService;
