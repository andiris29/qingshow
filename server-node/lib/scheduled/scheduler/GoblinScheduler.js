var async = require('async');
var _ = require('underscore');
var mongoose = require('mongoose');

var ItemSyncService = require('../goblin-item/ItemSyncService');
var ItemSourceUtil = require('./ItemSourceUtil');
var Items = require('../../model/items');
var ServerError = require('../../httpserver/server-error');

var GoblinScheduler = module.exports;


var allocatedRequestedItems = [];
var allocatedSecondaryItems = [];

var requestedItems = [];  //收到主动请求爬取的items
var secondaryItems = [];  //其余item队列

var itemIdToHandlers = {}; //itemId<String> => [callback]
var itemIdToAllocatedDate = {}; // itemId<String> => Date, 记录item分配出去的时间，防止某个item被分配后没有处理


/**
 * 请求下一个需要爬的item
 * @param type ItemSourceType combination, all type if type === null or undefined
 * @param callback function (err, item)
 */
GoblinScheduler.nextItem = function (type, callback) {
    var allItems = requestedItems.concat(secondaryItems);
    var i, matchedItem = null, tempItem = null;
    for (i = 0; i < allItems.length; i++) {
        tempItem = allItems[i];
        if (ItemSourceUtil.matchType(tempItem && tempItem.source, type)) {
            matchedItem = tempItem;
            break;
        }
    }
    if (!matchedItem) {
        //TODO 没有匹配item时暂时不query更多item, 返回错误
        callback(ServerError.fromCode(ServerError.ItemNotExist));
    } else {
        if (i < requestedItems.length) {
            requestedItems.splice(i, 1);
            allocatedRequestedItems.push(matchedItem);
        } else {
            secondaryItems.splice(i - requestedItems.length, 1);
            allocatedSecondaryItems.push(matchedItem);
        }
        var itemIdStr = _idToString(matchedItem._id);
        itemIdToAllocatedDate[itemIdStr] = new Date();

        callback(null, matchedItem);
    }
};


/**
 * 登记下一个需要爬的itemId及爬虫成功的回调函数
 * @param itemId
 * @param callback function (err, item)
 */
GoblinScheduler.registerItemWithId = function (itemId, callback) {
    var item = null;
    async.waterfall([
        function (callback) {
            _queryItemWithId(itemId, callback);
        }
    ], function (err, i) {
        if (err) {
            callback(err, item);
        } else {
            GoblinScheduler.registerItem(i, callback);
        }

    });
};

GoblinScheduler.registerItem = function (item, callback) {
    if (!ItemSyncService.isOutDate(item)) {
        // 该Item最近已经爬过，不需要再爬，直接执行callback
        callback(null, item);
        return;
    }
    //检查item是否已经在队列
    var allItems = allocatedRequestedItems.concat(allocatedSecondaryItems)
        .concat(requestedItems)
        .concat(secondaryItems);
    var itemIdStr = _idToString(item._id);

    var itemIndex = _findItemIndexWithId(allItems, itemIdStr);
    if (itemIndex !== -1) {
        //item已经在队列中
        var secondaryIndex = _findItemIndexWithId(secondaryItems, itemIdStr);
        if (secondaryIndex !== -1) {
            //item在secondary队列中，需要移到requested队列
            secondaryItems.splice(secondaryIndex, 1);
            requestedItems.push(itemIdStr);
        }
    } else {
        //item不再队列中，加入队列
        requestedItems.push(itemIdStr);
    }
    //记录handler
    var handlerArray = itemIdToHandlers[itemIdStr] || [];
    handlerArray.push(callback);
    itemIdToHandlers[itemIdStr] = handlerArray;
};


GoblinScheduler.finishItem = function (itemId, err, callback) {
    //finish暂时不check该item是否真被被爬好
    var allArrays = [allocatedRequestedItems,
        allocatedSecondaryItems,
        requestedItems,
        secondaryItems];
    var itemIdStr = _idToString(itemId);
    allArrays.forEach(function (array) {
        var index = _findItemIndexWithId(array, itemIdStr);
        if (index !== -1) {
            array.splice(index, 1);
        }
    });
    delete itemIdToAllocatedDate[itemIdStr];
    _invokeHandlerForItem(itemId, err, callback);
};

//TODO schedule扫描已分配item是否超时


/**
 * 执行itemId对应的所有register的handler
 * @param itemId
 * @param err
 * @param callback function (err, item)
 * @private
 */
var _invokeHandlerForItem = function (itemId, err, callback) {
    var itemIdStr = _idToString(itemId);
    var handlers = itemIdToHandlers[itemIdStr] || [];
    delete itemIdToHandlers[itemIdStr];

    _queryItemWithId(itemId, function (innerErr, item) {
        var tasks = [];
        handlers.forEach(function (h) {
            var task = function (callback) {
                h(innerErr, item);
                callback();
            };
            tasks.push(task);
        });
        async.parallel(tasks, function (err) {
            callback(err, item);
        });
    });
};


//////////
// Utility
var _parseId = function (itemId) {
    if (_.isString(itemId)) {
        return new mongoose.Types.ObjectId(itemId);
    } else {
        return itemId;
    }
};

var _idToString = function (itemId) {
    if (_.isString(itemId)) {
        return itemId;
    } else {
        return itemId.toString();
    }
};

var _findItemIndexWithId = function (itemArray, otherItemId) {
    otherItemId = _idToString(otherItemId);
    var item, i;
    for (i = 0; i < itemArray.length, i++) {
        item = itemArray[i];
        if (_idToString(item._id) === otherItemId) {
            return i;
        }
    }
    return -1;
};

var _queryItemWithId = function (itemId, callback) {
    async.waterfall([
        function (callback) {
            Items.findOne({
                _id : _parseId(itemId)
            }, callback);
        }, function (i, callback) {
            if (!i) {
                callback(ServerError.fromCode(ServerError.ItemNotExist));
            } else {
                callback(null, i);
            }
        }
    ], callback);
};
///////


