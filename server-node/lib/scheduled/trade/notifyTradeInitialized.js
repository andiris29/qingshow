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
        trades.filter(function(trade){
            return today - trade.update > 24 * 3600 
        }).forEach(function(trade, index) {
            async.waterfall([function(cb){
                People.findOne({
                    '_id' : trade.ownerRef
                }).exec(cb);
            }, function(people, cb){
                var verify = people.unreadNotifications.forEach(function(unread){
                    var extra = unread.extra;
                    if(extra._id.toString() === trade._id.toString()){
                        if (extra.command === NotificationHelper.CommandItemExpectablePriceUpdated) {
                            NotificationHelper.notify([trade.ownerRef], extra.command, {
                                '_id' : trade._id,
                                'command' : NotificationHelper.MessageItemPriceChanged
                            }, null);   
                        } else if(extra.command === NotificationHelper.CommandTradeInitialized){
                            NotificationHelper.notify([trade.ownerRef], extra.command, {
                                '_id' : trade._id,
                                'command' : NotificationHelper.MessageTradeInitialized
                            }, null); 
                        }
                    };
                });
                cb(null, trade);
            }], function(err, trade){
                callback(err, trade);
            })
        });
    }],
    function(err, trades) {
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

