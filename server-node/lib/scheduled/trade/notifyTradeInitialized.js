var schedule = require('node-schedule');
var winston = require('winston');

var async = require('async');
var winston = require('winston');
var _ = require('underscore');
var PushNotificationHelper = require('../../httpserver/helpers/PushNotificationHelper');
var Trade = require('../../model/trades');
var jPushAudiences = require('../../model/jPushAudiences');

var _next = function(today) {
    async.waterfall([
    function(callback) {
        Trade.find({
            'status' : 1 
        }).exec(callback);
    },
    function(trades, callback) {
        trades.forEach(function(trade, index) {
            jPushAudiences.find({
                'peopleRef' : trade.ownerRef
            }).exec(function(err, infos) {
                if (infos.length > 0) {
                    var targets = [];
                    infos.forEach(function(element) {
                        if (element.registrationId && element.registrationId.length > 0) {
                            targets.push(element.registrationId);
                        }
                    });

                    PushNotificationHelper.push(targets, PushNotificationHelper.MessageTradeInitialized, {
                        'id' : trade._id.toString(),
                        'command' : PushNotificationHelper.CommandTradeInitialized
                    }, null);
                }
            });
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
    rule.hour = 9;
    rule.minute = 0;
    schedule.scheduleJob(rule, function () {
        _run();
    });
    _run();
};

