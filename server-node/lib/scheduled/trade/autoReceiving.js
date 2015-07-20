var schedule = require('node-schedule');
var winston = require('winston');

var async = require('async');
var winston = require('winston');
var _ = require('underscore');

var TradeHelper = require('../../httpserver/helpers/TradeHelper');
var Trade = require('../../model/trades');

var _next = function(today) {
    var halfMonthBefor = today.setDate(today.getDate() - 14);
    async.waterfall([
    function(callback) {
        Trade.find({
            'status' : { 
                '$in' : [3, 14]
            }
        }).exec(callback);
    },
    function(trades, callback) {
        var targets = _.filter(trades, function(trade) {
            if (trade.statusLogs == null || trade.statusLogs.length == 0) {
                return false;
            }
            var lastlog = trade.statusLogs[trade.statusLogs.length - 1];
            return lastlog.update < halfMonthBefor;
        });

        var tasks = targets.map(function(trade) {
            return function(callback) {
                winston.info('[Trade-autoReceived] ' + trade._id.toString() + ' change to received');
                TradeHelper.updateStatus(trade, 15, 'autoReceived', null, callback);
            };
        });
        async.parallel(tasks, function(err) {
            callback(null, targets);
        });
    }],
    function(err, trades) {
        winston.info('Trade-autoReceiving complete');
    });
};

var _run = function() {
    var startDate = new Date();
    winston.info('Trade-autoReceiving run at: ' + startDate);

    _next(startDate);
};

module.exports = function () {
    var rule = new schedule.RecurrenceRule();
    rule.hour = 2;
    rule.minute = 0;
    schedule.scheduleJob(rule, function () {
        _run();
    });
    _run();
};
