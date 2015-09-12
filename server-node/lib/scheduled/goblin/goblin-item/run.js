//TODO remove this file

var schedule = require('node-schedule');

var path = require('path');
var fs = require('fs');
var winston = require('winston');
var async = require('async');
var _ = require('underscore');
var moment = require('moment');

var Item = require('../../../model/items');
var GoblinError = require('../common/GoblinError');
var mongoose = require('mongoose');
var qsmail = require('../../../runtime/qsmail');
var ItemSyncService = require('./../common/ItemSyncService');



var _next = function (time, config) {
    var report = "";
    async.waterfall([
        function (callback) {
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
//            var criteria = {'_id' : new mongoose.Types.ObjectId('55b1dc9b38dadbed5a99a812')};

            Item.find(criteria).sort({
                'sync' : 1
            }).exec(function (err, items) {
                if (err) {
                    callback(err);
                } else if (!items || !items.length) {
                    callback(GoblinError.fromCode(GoblinError.NoItemShouldBeCrawl));
                } else {
                    items = items.filter(function (item) {
                        if (!item) {
                            return false;
                        }
                        return ItemSyncService.canParseItemSource(item.source);
                    });

                    winston.info('[Goblin-tbitem] Total count: ' + items.length);
                    callback(null, items);
                }
            });
        },
        function (items, callback) {
            var tasks = [];
            items.forEach(function (item) {
                var task = function (callback) {
                    ItemSyncService.syncItem(item, function (err, item) {
                        var reportContent = [];
                        if (err) {
                            reportContent = [new Date(), item._id, err.description, null, item.source];
                            report += reportContent.join(',') + '\n';
                        } else {
                            reportContent = [new Date(), item._id, 'success', 'promoPrice:' + item.promoPrice + ' price:' + item.price, item.source];
                            report += reportContent.join(',') + '\n';
                        }
                        //Goblin Item Will Only Record Error in Report
                        setTimeout(function () {
                            callback();
                        }, _.random(5000, 10000));

                    });
                };
                tasks.push(task);
            });
            async.series(tasks, callback);
        }
    ], function (err) {
        winston.info('[Goblin-tbitem] Complete.');

        var date = moment();
        var dateStr = date.format("YYYY-MM-DD__HH_mm_ss");

        var fileName = 'gobin_report_' + dateStr + '.txt';
        if (config.reportDirectory) {
            var fullPath = path.join(config.reportDirectory, fileName);
            fs.writeFileSync(fullPath, report);
            qsmail.send('商品爬虫总结' + dateStr, fullPath);
        } else {
            qsmail.send('商品爬虫总结' + dateStr, 'error: unknown report directory');
        }

    });
};

var _run = function (config) {
    var startDate = new Date();
    // startDate.setHours(startDate.getHours() - 6);
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
    // _run(config);
};

