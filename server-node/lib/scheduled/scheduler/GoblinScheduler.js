
var async = require('async');
var _ = require('underscore');
var mongoose = require('mongoose');

var ItemSyncService = require('../goblin-item/ItemSyncService');
var Items = require('../../model/items');
var ServerError = require('../../httpserver/server-error');

var GoblinScheduler = module.exports;


var allocatedItems = [];

var requestedItems = [];  //收到主动请求爬取的itemId
var secondaryItems = [];  //其余item队列

var itemIdToHandlers = {}; //itemId<String> => [callback]


/**
 * 请求下一个需要爬的item
 * @param type ItemSourceType combination, all type if type === null or undefined
 * @param callback function (err, item)
 */
GoblinScheduler.nextItem = function (type, callback) {
//TODO
};


/**
 * 登记下一个需要爬的itemId及爬虫成功的回调函数
 * @param itemId
 * @param callback function (err, item)
 */
GoblinScheduler.registerItemWithId = function (itemId, callback) {
    var item = null;
    //TODO replace with _queryItemWithId
    async.waterfall([
        function (callback) {
            Items.findOne({
                _id : _parseId(itemId)
            }, callback);
        }, function (i, callback) {
            item = i;
            if (!i) {
                callback(ServerError.fromCode(ServerError.ItemNotExist));
            } else {
                callback(null, i);
            }
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
    //TODO 加入队列


};


GoblinScheduler.finishItem = function (itemId, err, callback) {
    //TODO
    async.waterfall([
        function (callback) {

        }, function (callback) {

        }
    ], function (err) {

    });


    _invokeHandlerForItem(itemId, err, function () {
        //callback(err, item);
    });
};


/**
 * 执行itemId对应的所有register的handler
 * @param itemId
 * @param err
 * @param callback
 * @private
 */
var _invokeHandlerForItem = function (itemId, err, callback) {
    var tasks = [];
    if (!_.isString(itemId)) {
        itemId = itemId.toString();
    }
    var handlers = itemIdToHandlers[itemId] || [];
    delete itemIdToHandlers[itemId];

    handlers.forEach(function (h) {
        var task = function (callback) {
            h(err, item);
            callback();
        };
        tasks.push(task);
    });
    async.parallel(tasks, callback);
};




// Utility
var _parseId = function (itemId) {
    if (_.isString(itemId)) {
        return new mongoose.Types.ObjectId(string);
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

    for (var i = 0; i < itemArray.length, i++) {
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
            item = i;
            if (!i) {
                callback(ServerError.fromCode(ServerError.ItemNotExist));
            } else {
                callback(null, i);
            }
        }
    ], callback);
};