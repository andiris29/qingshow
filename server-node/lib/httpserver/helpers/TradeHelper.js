var mongoose = require('mongoose');
var async = require('async');

var People = require('../../model/peoples');
var Trade = require('../../model/trades');

var TradeHelper = module.exports;

TradeHelper.updateStatus = function(trade, updateStatus, peopleId, callback) {
    var statusLog = {
        'status' : updateStatus,
        peopleRef : peopleId,
        date : Date.now
    };
    trade.set('status', updateStatus);
    if (trade.statusLogs) {
        trade.statusLogs = [];
    }
    trade.statusLogs.push(statusLog);

    trade.save(callback);
}
