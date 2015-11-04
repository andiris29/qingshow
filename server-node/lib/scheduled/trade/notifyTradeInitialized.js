var schedule = require('node-schedule');
var winston = require('winston');

var async = require('async');
var winston = require('winston');
var _ = require('underscore');
var NotificationHelper = require('../../helpers/NotificationHelper');
var Trade = require('../../dbmodels').Trade;
var jPushAudiences = require('../../dbmodels').JPushAudience;
var People = require('../../dbmodels').People;

var _next = function(today) {
    async.waterfall([
    function(callback) {
        Trade.find({
            'status' : 1 
        }).exec(callback);
    },
    function(trades, callback) {
        var notifiyTasks = trades.filter(function(trade){
            return today - trade.update > 24 * 3600 * 1000;
        }).map(function(trade, index) {
            return function(cb2){
                async.waterfall([function(cb){
                    People.findOne({
                        '_id' : trade.ownerRef
                    }).exec(cb);
                }, function(people, cb){
                    people.unreadNotifications.forEach(function(unread){
                        var extra = unread.extra;
                        if (extra == null) {
                            return;
                        }
                        if (extra._id == null || extra._id.length === 0) {
                            return;
                        }
                        if(extra._id.toString() === trade._id.toString()){
                            if (extra.command === NotificationHelper.CommandItemExpectablePriceUpdated || extra.command === NotificationHelper.CommandTradeInitialized) {
                                NotificationHelper.notify([trade.ownerRef], NotificationHelper.MessageItemPriceChanged, {
                                    '_id' : trade._id,
                                    'command' : extra.command
                                }, function(){});
                            }
                        };
                    });
                    cb();
                }], cb2);
            };
        });
        async.parallel(notifiyTasks, callback);
    }],
    function(err, result) {
        winston.info('Trade-autoReceiving complete');
    });
};

var _run = function() {
    var startDate = new Date();
    winston.info('Trade-notifyTradeInitialized run at: ' + startDate);

    _next(startDate);
};

module.exports = function () {
    var rule = new schedule.RecurrenceRule();
    rule.minute = 0;
    schedule.scheduleJob(rule, function () {
        _run();
    });
};

