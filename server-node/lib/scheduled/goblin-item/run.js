var schedule = require('node-schedule');
var winston = require('winston');
var path = require('path');
var fs = require('fs');

var async = require('async');
var winston = require('winston');
var _ = require('underscore');

var TaobaoWebItem = require('./taobao/Item');
var HmWebItem = require('./hm/Item');
var JamyWebItem = require('./jamy/Item');
var URLParser = require('./URLParser');

var Item = require('../../model/items');
// TODO Remove dependency on httpserver
var ServerError = require('../../httpserver/server-error');
var mongoose = require('mongoose');
var qsmail = require('../../runtime/qsmail');

var _next = function (time, config) {
    var report = "";
    async.waterfall([
        function (callback) {
            var criteria = {
                '$and': [{
                    '$or': [{
                        'goblinUpdate': {
                            '$exists': false
                        }
                    }, {
                        'goblinUpdate': {
                            '$lt': time
                        }
                    }]
                }]
            };
//            var criteria = {'_id' : new mongoose.Types.ObjectId('55b1dc9b38dadbed5a99a812')};

            Item.find(criteria, function (err, items) {
                if (err) {
                    callback(err);
                } else if (!items || !items.length) {
                    callback(ServerError.fromCode(ServerError.PagingNotExist));
                } else {
                    items = items.filter(function (item) {
                        if (!item.source) {
                            return false;
                        }

                        return URLParser.isFromTaobao(item.source) || URLParser.isFromTmall(item.source) || 
                                URLParser.isFromHm(item.source) || URLParser.isFromJamy(item.source);
                    });

                    winston.info('[Goblin-tbitem] Total count: ' + items.length);
                    callback(null, items);
                }
            });
        },
        function (items, callback) {
            var tasks = [];


            items.forEach(function (item) {
                if (!item) {
                    return;
                }
                var task = function (callback) {
                    var crawlCallback = function (err) {
                        var reportContent = [];
                        if (err) {
                            reportContent = [new Date(), item._id, err, null, item.source];
                            report += reportContent.join(',') + '\n';

                            item.delist = new Date();
                            item.save(function (err) {
                                setTimeout(function () {
                                    callback(err);
                                }, _.random(5000, 10000));
                            });
                        } else {
                            reportContent = [new Date(), item._id, 'success', 'promoPrice:' + item.promoPrice + ' price:' + item.price, item.source];
                            report += reportContent.join(',') + '\n';
                            setTimeout(function () {
                                callback(err);
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
                tasks.push(task);
            });
            async.series(tasks, callback);
        }
    ], function (err) {
        winston.info('[Goblin-tbitem] Complete.');
        var date = new Date();
        var fileName = 'gobin_report_' + date + '.txt';
        if (config.reportDirectory) {
            var fullPath = path.join(config.reportDirectory, fileName);
            fs.writeFileSync(fullPath, report);
            qsmail.send('商品爬虫总结' + new Date(), fullPath);
        } else {
            qsmail.send('商品爬虫总结' + new Date(), 'error: unknown report directory');
        }

    });
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
                item.goblinUpdate = new Date();
                _logItem('item success', item);
                item.save(callback);
            }

        }
    });
};

var _crawlItemHmInfo = function(item, callback) {
    async.waterfall([function(callback) {
        HmWebItem.getSkus(item.source, function(err, hmInfo) {
            callback(err, hmInfo);
        });
    }], function(err, hmInfo) {
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
                item.goblinUpdate = new Date();
                _logItem('item success', item);
                item.save(callback);
            }

        }
    });
};

var _crawlItemJamyInfo = function(item, callback) {
    async.waterfall([function(callback) {
        JamyWebItem.getSkus(item.source, function(err, jamyInfo) {
            callback(err, jamyInfo);
        });
    }], function(err, jamyInfo) {
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
                item.goblinUpdate = new Date();
                _logItem('item success', item);
                item.save(callback);
            }
        }
    });
};

var _logItem = function (content, item) {
    winston.info('[Goblin-tbitem] ' + content + ': ' + item._id);
};

var _run = function (config) {
    var startDate = new Date();
//    startDate.setDate(startDate.getDate() - 1);
    winston.info('Goblin-tbitem run at: ' + startDate);

    _next(startDate, config);
};

module.exports = function (config) {
    var rule = new schedule.RecurrenceRule();
    rule.hour = 1;
    rule.minute = 0;
    schedule.scheduleJob(rule, function () {
        _run(config);
    });
    _run(config);
};

