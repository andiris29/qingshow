var schedule = require('node-schedule');
var winston = require('winston');

var async = require('async');
var winston = require('winston');
var _ = require('underscore');

var TaobaoWebItem = require('../../taobao/web/Item');

var Item = require('../../model/items');
// TODO Remove dependency on httpserver
var MongoHelper = require('../../httpserver/helpers/MongoHelper');
var ServerError = require('../../httpserver/server-error');
var mongoose = require('mongoose');

var _next = function (time) {
    async.waterfall([
        function (callback) {
            var criteria = {
                '$and': [{
                    '$or': [{
                        'taobaoInfo.refreshTime': {
                            '$exists': false
                        }
                    }, {
                        'taobaoInfo.refreshTime': {
                            '$lt': time
                        }
                    }, {
                        'deactive': {
                            '$exists': false
                        }
                    }, {
                        'deactive': true
                    }]
                }]
            };

            var query = Item.find(criteria, function (err, items) {
                if (err) {
                    callback(err);
                } else if (!items || !items.length) {
                    callback(ServerError.fromCode(ServerError.PagingNotExist));
                } else {
                    winston.info('[Goblin-tbitem] Total count: ' + items.length);
                    callback(null, items);
                }
            });
        },
        function (items, callback) {
            var tasks = [];
            items.forEach(function (item) {
                var task = function (callback) {
                    _logItem('item start', item);
                    _crawlItemTaobaoInfo(item, function(err) {
                        setTimeout(function() {
                            callback(err);
                        }, _.random(5000, 10000));
                    });
                };
                tasks.push(task);
            });
            async.series(tasks, callback);
        }
    ], function (err) {
        winston.info('[Goblin-tbitem] Complete.');
    });
};

var _crawlItemTaobaoInfo = function (item, callback) {
    async.waterfall([
        function (callback) {
            TaobaoWebItem.getSkus(item.source, function (err, taobaoInfo) {
                callback(err, taobaoInfo);
            });
        }
    ], function (err, taobaoInfo) {
        if (err) {
            _logItem('item error', item);
            callback(err);
        } else {
            if (!taobaoInfo) {
                item.taobaoInfo = item.taobaoInfo || {};
                item.taobaoInfo.refreshTime = new Date();
                item.deactive = true;
                _logItem('item failed', item);
            } else {
                item.taobaoInfo = taobaoInfo;
                item.taobaoInfo.refreshTime = new Date();
                item.deactive = false;
                _logItem('item success', item);
            }
            item.save(callback);
        }
    });
};

var _logItem = function(content, item) {
    winston.info('[Goblin-tbitem] ' + content + ': ' + item._id);
};

var _run = function() {
    var startDate = new Date();
    startDate.setDate(startDate.getDate() - 3);
    winston.info('Goblin-tbitem run at: ' + startDate);

    _next(startDate);
};

module.exports = function () {
    var rule = new schedule.RecurrenceRule();
    rule.hour = 1;
    rule.minute = 0;
    schedule.scheduleJob(rule, function () {
        _run();
    });
    // _run();
};
