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
                    }]
                }, {
                    // '$or': [{
                        // 'deactive': {
                            // '$exists': false
                        // }
                    // }, {
                        // 'deactive': false
                    // }]
                }]
            };

            var query = Item.find(criteria);
            var queryCount = Item.find(criteria);
            var pageNo = 1;
            var pageSize = 1;
            MongoHelper.queryPaging(query, queryCount, pageNo, pageSize, function (err, result, count) {
                winston.info('goblin item : remain item ' + count);
                if (err) {
                    callback(err);
                } else if (!count) {
                    callback(ServerError.PagingNotExist);
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
                    _crawlItemTaobaoInfo(item, function(err) {
                        setTimeout(function() {
                            callback(err);
                        }, 1000);
                    });
                };
                tasks.push(task);
            });
            async.series(tasks, callback);

        }
    ], function (err) {
        //TODO
        if (err === ServerError.PagingNotExist) {
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
                _.delay(function() {
                    _next(time, retryTime);
                }, 2000);
            } else {
                winston.info('Error: goblin item retry time is zero, stop.');
            }
        } else {
            _.delay(function () {
                _next(time, retryTime);
            }, 2000);
        }
    });
};

var _crawlItemTaobaoInfo = function (item, callback) {
    async.waterfall([
        function (callback) {
            TaobaoWebItem.getSkus(item.source, function (err, taobaoInfo) {
                item.taobaoInfo = taobaoInfo;
                callback();
            });
        }
    ], function (err) {
        if (err) {
            winston.info('[Goblin-tbitem] item error: ' + item._id);
            callback(err);
        } else {
            item.taobaoInfo = item.taobaoInfo || {};
            if (Object.keys(item.taobaoInfo).length === 0) {
                item.deactive = true;
                winston.info('[Goblin-tbitem] item failed: ' + item._id);
            } else {
                item.deactive = false;
                winston.info('[Goblin-tbitem] item success: ' + item._id);
            }
            item.taobaoInfo.refreshTime = new Date();
            item.save(callback);
        }
    });
};

module.exports = function () {
    var rule = new schedule.RecurrenceRule();
    rule.hour = 1;
    rule.minute = 0;
    schedule.scheduleJob(rule, function () {
        var startDate = new Date();
        winston.info('Goblin-tbitem run at: ' + startDate);

        _next(startDate, 10);
    });

    var startDate = new Date();
    _next(startDate, 10);
};
