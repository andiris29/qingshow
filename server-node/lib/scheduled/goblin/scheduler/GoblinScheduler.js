var async = require('async');
var _ = require('underscore');
var mongoose = require('mongoose');

var ItemSyncService = require('../common/ItemSyncService');
var ItemSourceUtil = require('../common/ItemSourceUtil');
var Items = require('../../../model/items');
var GoblinError = require('../common/GoblinError');

var GoblinScheduler = module.exports;


var allocatedRequestedItems = [];
var allocatedSecondaryItems = [];

var requestedItems = [];  //收到主动请求爬取的items
var secondaryItems = [];  //其余item队列

var allArrays = [
    allocatedRequestedItems,
    allocatedSecondaryItems,
    requestedItems,
    secondaryItems
];


var itemIdToHandlers = {}; //itemId<String> => [callback]
var itemIdToAllocatedDate = {}; // itemId<String> => Date, 记录item分配出去的时间，防止某个item被分配后没有处理


GoblinScheduler.start = function (config) {
    //TODO save config, add schedule
    _checkToQueryNewItems();
    _timeoutScheduler();
};


//定时扫描已分配item是否超时，如果超时则放回待分配数组重新分配
var timeoutCheckDuration = 60 * 1000; //TODO move to config,暂时hard code 1 分钟
var timeoutDuration = 30 * 1000; // TODO move to config
var _timeoutScheduler = function () {
    var now = new Date();
    for (var itemId in itemIdToAllocatedDate) {
        if (itemIdToAllocatedDate.hasOwnProperty(itemId)) {
            var setupTime = itemIdToAllocatedDate[itemId];
            if (now - setupTime > timeoutDuration) {
                _handlerTimeout(itemId);
            }
        }
    }
    setTimeout(function () {
        _timeoutScheduler();
    }, timeoutCheckDuration);
};

var _handlerTimeout = function(itemId) {
    _rollbackAllocatedItem(itemId, allocatedRequestedItems, requestedItems);
    _rollbackAllocatedItem(itemId, allocatedSecondaryItems, secondaryItems);
    delete itemIdToAllocatedDate[itemId];
};

var _rollbackAllocatedItem = function (itemId, allocatedArray, sourceArray) {
    var index = _findItemIndexWithId(itemId, allocatedArray);
    if (index !== -1) {
        var item = allocatedArray(index);
        allocatedArray.splice(index, 1);
        sourceArray.unshift(item);   //rollback 回待分配数组的item优先分配
    }
};


/**
 * 请求下一个需要爬的item
 * @param type ItemSourceType combination, all type if type === null or undefined
 * @param callback function (err, item)
 */
GoblinScheduler.nextItem = function (type, callback) {
    var allItems = requestedItems.concat(secondaryItems);
    _fetchNextItem(allItems, type, callback);
};

GoblinScheduler.nextRequestedItem = function (type, callback) {
    _fetchNextItem(requestedItems, type, callback);
};

var _fetchNextItem = function (scope, type, callback) {
    var i, matchedItem = null, tempItem = null;
    for (i = 0; i < scope.length; i++) {
        tempItem = scope[i];
        if (!type || ItemSourceUtil.matchType(tempItem && tempItem.source, type)) {
            matchedItem = tempItem;
            break;
        }
    }

    if (!matchedItem) {
        //没有匹配item时暂时不等待, 直接返回错误并异步query新的item
        callback(GoblinError.fromCode(GoblinError.NoItemShouldBeCrawl));
        _checkToQueryNewItems();
    } else {
        var tempIndex;
        tempIndex = requestedItems.indexOf(matchedItem);
        if (tempIndex !== -1) {
            requestedItems.splice(tempIndex, 1);
            allocatedRequestedItems.push(matchedItem);
        }

        tempIndex = secondaryItems.indexOf(matchedItem);
        if (tempIndex !== -1) {
            secondaryItems.splice(tempIndex, 1);
            allocatedSecondaryItems.push(matchedItem);
        }
        var itemIdStr = _idToString(matchedItem._id);
        itemIdToAllocatedDate[itemIdStr] = new Date();
        callback(null, matchedItem);
        _checkToQueryNewItems();
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
    if (!ItemSyncService.canParseItemSource(item.source)) {
        //ItemSource类型不支持
        callback(GoblinError.fromCode(GoblinError.NotSupportItemSource), item);
        return;
    }


    //检查item是否已经在队列
    var allItems = allArrays.reduce(function (l, r) { return l.concat(r);}, []);

    var itemIdStr = _idToString(item._id);

    var itemIndex = _findItemIndexWithId(allItems, itemIdStr);
    if (itemIndex !== -1) {
        //item已经在队列中
        var secondaryIndex = _findItemIndexWithId(secondaryItems, itemIdStr);
        if (secondaryIndex !== -1) {
            //item在secondary队列中，需要移到requested队列
            secondaryItems.splice(secondaryIndex, 1);
            requestedItems.push(item);
        }
    } else {
        //item不再队列中，加入队列
        requestedItems.push(item);
        //trigger goblin main slaver，令其主动爬取item
        require('../slaver/GoblinMainSlaver').tregger(item);

    }
    //记录handler
    var handlerArray = itemIdToHandlers[itemIdStr] || [];
    handlerArray.push(callback);
    itemIdToHandlers[itemIdStr] = handlerArray;
};


GoblinScheduler.finishItem = function (itemId, err, callback) {
    //finish暂时不check该item是否真被被爬好

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





var isQueryNewItems = false;
var secondaryQueueMinSize = 200;
var querySize = 200;
var lastAllItemDate = null;
var allItemDuration = 5 * 60 * 1000;   //如果所有需要爬的item都进入队列，则5分钟重新检查一次是否有新的item需要被爬
/**
 * 检查是否需要往secondary队列中添加新的item，若需要，则查找并添加
 */
var _checkToQueryNewItems = function (callback) {
    if (isQueryNewItems) {
        return;
    }
    if (secondaryItems.length > secondaryQueueMinSize) {
        return;
    }
    if (lastAllItemDate && new Date() - lastAllItemDate < allItemDuration) {
        //距离上次所有item都被爬取的时间间隔太短
        return;
    }
    var totalCount = allArrays.reduce(function (l, r) { return l + r.length;}, 0);

    var time = new Date() - ItemSyncService.outDateDuration;
    var criteria = {
        '$and': [{
            '$or': [{
                'sync': {
                    '$exists': false
                }
            }, {
                'sync': {
                    '$lt': time
                }
            }]
        }, {
            '$or' : [{
                'syncEnabled': {
                    '$exists': false
                }
            }, {
                'syncEnabled' : true
            }]
        }]
    };
    isQueryNewItems = true;
    async.waterfall([
        function (callback) {
            Items.find(criteria)
                .count()
                .exec(callback);
        }, function (count, callback) {
            if (count < totalCount) {
                //所有需要爬的item都在队列中，不需要继续query

                lastAllItemDate = new Date();
                callback('all item in the queue');
            } else {
                callback();
            }
        }, function (callback) {
            //查询新item
            //TODO 根据feeding/hot feeding/new等顺序进行查询
            Items.find(criteria)
                .limit(querySize + totalCount) //查找querySize + totalCount个item以保证有新item
                .exec(callback);
        }, function (items, callback) {
            //将新item加入队列

            //去除已经在队列中的item
            var allItemIds =
                allArrays
                    .reduce(function (l, r) { return l.concat(r);}, [])
                    .map(function (i) { return _idToString(i._id); });
            var newItems = items.filter(function (i) {
                return allItemIds.indexOf(_idToString(i._id)) === -1;
            });
            newItems = items.filter(function (i) {
                //去除不支持的item
                return ItemSyncService.canParseItemSource(i.source);
            });

            secondaryItems = secondaryItems.concat(newItems);
            callback();
        }
    ], function (err) {
        isQueryNewItems = false;
        if (callback) {
            callback(err);
        }
    });

};


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
    if (!itemId) {
        return itemId;
    } else if (_.isString(itemId)) {
        return new mongoose.Types.ObjectId(itemId);
    } else {
        return itemId;
    }
};

var _idToString = function (itemId) {
    if (!itemId) {
        return itemId;
    } else if (_.isString(itemId)) {
        return itemId;
    } else {
        return itemId.toString();
    }
};

var _findItemIndexWithId = function (itemArray, otherItemId) {
    otherItemId = _idToString(otherItemId);
    var item, i;
    for (i = 0; i < itemArray.length; i++) {
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
                callback(GoblinError.fromCode(GoblinError.ItemNotExist));
            } else {
                callback(null, i);
            }
        }
    ], callback);
};
///////


