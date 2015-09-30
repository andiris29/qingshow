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
            return new Date() - trade.update > 24 * 3600 
        }).forEach(function(trade, index) {
            NotificationHelper.notify([trade.ownerRef], NotificationHelper.MessageTradeInitialized, {
                '_id' : trade._id,
                'command' : NotificationHelper.CommandTradeInitialized
            }, null);  
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

