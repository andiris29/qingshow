var winston = require('winston');
var async = require('async');
var _ = require('underscore');

var Item = require('../../../dbmodels').Item,
    People = require('../../../dbmodels').People;

var crypto = require('crypto'), _secret = 'qingshow@secret';

var GoblinError = require('../../../goblin-slave/GoblinError'),
    ItemSourceUtil = require('../../../goblin-slave/ItemSourceUtil'),
    ItemSourceType = require('../../../goblin-slave/ItemSourceType');

var ItemSyncService = {};

ItemSyncService.isOutDate = function (item) {
    if (!item || !item.sync) {
        return true;
    }
    var now = new Date();
    // 暂定为 24小时 需要重新sync
    return ((now - item.sync) > ItemSyncService.outDateDuration);
};

ItemSyncService.outDateDuration = 1000 * 60 * 60 * 24;

ItemSyncService.syncItemInfo = function(item, itemInfo, err, callback) {
    if (err && _.isString(err)) {
        err = GoblinError.fromDescription(err);
    }

    if (err) {
        callback(err, item);
    } else if (!itemInfo) {
        item.delist = new Date();
        item.save(function (err) {
            callback(err, item);
        });
    } else {
        async.waterfall([
            function(callback){
                callback(null, item, itemInfo);
            },
            _updateShopInfo,
            _updateItem,
        ], function(err) {
            item.sync = new Date();
            item.save(function (innerErr) {
                callback(innerErr || err, item);
            });
        });
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


ItemSyncService.canParseItemSource = function (itemSourceStr) {
    if (!itemSourceStr) {
        return false;
    }
    var sourceType = ItemSourceUtil.getType(itemSourceStr);
    return !!sourceType;
};


var _updateShopInfo = function(item, itemInfo, callback) {
    var webItem = itemInfo;
    //taobaoInfo.shopId
    var shopInfo = webItem.shopInfo;
    if (shopInfo && shopInfo.shopId) {
        var shopId = shopInfo.shopId;
        async.waterfall([
            function (callback) {
                People.findOne({
                    'shopInfo.taobao.sid' : shopId
                }, callback);
            }, function (people, callback) {
                if (!people) {
                    new People({
                        nickname: shopInfo.shopName,
                        userInfo : {
                            id : shopId,
                            encryptedPassword : _encrypt(shopId)
                        },
                        shopInfo : {
                            taobao : {
                                sid : shopId
                            }
                        }
                    }).save(callback);
                } else {
                    callback(null, people, 0);
                }
            }, function (people, number, callback) {
                item.shopRef = people._id;
                item.save(callback);
            }], function (err, i, count) {
            callback(null, item, webItem);
        });
    } else{
        callback(null, item, webItem);
    }
};

var _updateItem = function(item, itemInfo, callback) {
    item.delist = null;
    item.price = itemInfo.price;
    item.promoPrice = itemInfo.promo_price;
    item.skuProperties = itemInfo.skuProperties;
    var skuTable = {};
    itemInfo.skuTable = itemInfo.skuTable || {};
    for (var key in itemInfo.skuTable) {
        if (itemInfo.skuTable.hasOwnProperty(key)) {
            var value = itemInfo.skuTable[key];
            // skuTable key should not contain '.' to avoid mongo error
            key = key.replace(/\./g, '');
            skuTable[key] = value;
        }
    }
    item.skuTable = skuTable;

    //minExpectedPrice
    var price = item.promoPrice || item.price;
    var sourceType = ItemSourceUtil.getType(item.source);
    if (price) {
        var discount;
        if (sourceType === ItemSourceType.Taobao || sourceType === ItemSourceType.Tmall)  {
            if (price <= 100) {
                discount = 0.5;
            } else if (price <= 180) {
                discount = 0.6;
            } else {
                discount = 0.7;
            }
        } else if (sourceType === ItemSourceType.Hm) {
            discount = 0.7;
        } else if (sourceType === ItemSourceType.Jamy) {
            discount = 0.6;
        }
        if (discount) {
            item.minExpectedPrice = price * discount;
        }
    }
    callback(null, itemInfo);
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
