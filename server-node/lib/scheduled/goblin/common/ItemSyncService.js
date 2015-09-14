var winston = require('winston');
var GoblinCrawler = require('./crawler/GoblinCrawler');

var async = require('async');
var URLParser = require('./URLParser');
var _ = require('underscore');
var GoblinError = require('./GoblinError');

var Item = require('../../../model/items');
var People = require('../../../model/peoples');

var crypto = require('crypto'), _secret = 'qingshow@secret';


var ItemSyncService = {};

ItemSyncService.isOutDate = function (item) {
    if (!item || !item.sync) {
        return true;
    }
    var now = new Date();
    // 暂定为 1小时 需要重新sync
    return ((now - item.sync) > ItemSyncService.outDateDuration);
};

ItemSyncService.outDateDuration = 1000 * 60 * 60 * 1;

/**
 *
 * @param item
 * @param callback function(err, item)
 */
ItemSyncService.syncItem = function (item, callback) {
    if (!item || !ItemSyncService.isOutDate(item)) {
        callback(null, item);
        return;
    }

    _logItem('item start', item);
    async.waterfall([
        function (callback) {
            GoblinCrawler.crawl(item.source, callback);
        }, function (webItem, callback) {
            //taobaoInfo.shopId
            var shopInfo = webItem.shopInfo;
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
                    }], function (err, i, count) {
                    callback(null, webItem);
                });
            } else{
                callback(null, webItem);
            }

        }
    ], function (goblinError, webItem) {
        if (goblinError) {
            //Delist
            item.delist = new Date();
            if (goblinError.errorCode === GoblinError.Delist) {
                _logItem('item success, delist', item);
            } else {
                //Other Error
                _logItem('item error', item);
            }
        } else {
            item.delist = null;
            item.price = webItem.price;
            item.promoPrice = webItem.promo_price;
            item.skuProperties = webItem.skuProperties;
            var skuTable = {};
            webItem.skuTable = webItem.skuTable || {};
            for (var key in webItem.skuTable) {
                if (webItem.skuTable.hasOwnProperty(key)) {
                    var value = webItem.skuTable[key];
                    // skuTable key should not contain '.' to avoid mongo error
                    key = key.replace(/\./g, '');
                    skuTable[key] = value;
                }
            }
            item.skuTable = skuTable;
            _logItem('item success', item);
        }
        item.sync = new Date();
        item.save(function (innerErr) {
            callback(innerErr || goblinError, item);
        });
    });
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


ItemSyncService.canParseItemSource = function (itemSourceStr) {
    return GoblinCrawler.canParseItemSource(itemSourceStr);
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
