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

var _next = function (time, retryTime) {
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

            var query = Item.find(criteria);
            var queryCount = Item.find(criteria);
            var pageNo = 1;
            var pageSize = 1;
            MongoHelper.queryPaging(query, queryCount, pageNo, pageSize, function (err, result, count) {
                winston.info('[Goblin-tbitem] remaining count: ' + count);
                if (err) {
                    callback(err);
                } else if (!count) {
                    callback(ServerError.fromCode(ServerError.PagingNotExist));
                } else {
                    callback(null, result);
                }
            });
        },
        function (result, callback) {
            items = result;
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
        if (err && err.errorCode === ServerError.PagingNotExist) {
            //finish
            winston.info('goblin item daily complete');
            return;
        }

        if (err) {
            winston.info('gobin item error:');
            winston.info(err);
            retryTime -= 1;
            if (retryTime > 0) {
                winston.info('remain retry : ' + retryTime);
                _next(time, retryTime);
            } else {
                winston.info('Error: goblin item retry time is zero, stop.');
            }
        } else {
            _next(time, retryTime);
        }
    });
};

var _crawlItemTaobaoInfo = function (item, callback) {
    async.waterfall([
        function (callback) {
            TaobaoWebItem.getSkus(item.source, function (err, taobaoInfo) {
                item.taobaoInfo = taobaoInfo;
                callback(err);
            });
        }
    ], function (err) {
        if (err) {
            _logItem('item error', item);
            callback(err);
        } else {
            item.taobaoInfo = item.taobaoInfo || {};
            item.taobaoInfo.refreshTime = new Date();
            if (Object.keys(item.taobaoInfo).length === 0) {
                item.deactive = true;
                _logItem('item failed', item);
                callback();
            } else {
                _logItem('item success', item);
                item.deactive = false;
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

    _next(startDate, 10);
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
