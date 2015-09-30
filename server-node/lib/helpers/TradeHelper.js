var mongoose = require('mongoose');
var async = require('async');

var qsmail = require('../runtime/').mail;
var winston = require('winston');
var RequestHelper = require('./RequestHelper');
var NotificationHelper = require('./NotificationHelper');

var TradeHelper = module.exports;

var _statusOrderMap = {
    1 : '00',
    2 : '10',
    3 : '10',
    5 : '20',
    7 : '10',
    9 : '20',
    10 : '20',
    15 : '20',
    17 : '20',
    18 : '30'
};

TradeHelper.updateStatus = function(trade, newStatus, comment, peopleId, callback) {
    winston.info('[TradeHelper.updateStatus] ' + trade._id.toString() + ':' + trade.status + '>' + newStatus);
    var statusLog = {
        'status' : newStatus,
        'comment' : comment,
        'peopleRef' : peopleId,
        'date' : Date.now
    };
    newStatus = Number.parseInt(newStatus);

    if (newStatus === 2 || newStatus === 18) {
        NotificationHelper.read([trade.ownerRef], {
            'extra.command' : NotificationHelper.CommandTradeInitialized,
            'extra._id' : trade._id
        }, function(err){})
    };

    if (newStatus === 5 || newStatus === 15 || newStatus === 7) {
        NotificationHelper.read([trade.ownerRef], {
            'extra.command' : NotificationHelper.CommandTradeShipped,
            'extra._id' : trade._id
        }, function(err){})
    };

    trade.set('status', newStatus);
    trade.statusLogs = trade.statusLogs || [];
    trade.statusLogs.push(statusLog);
    trade.statusOrder= _statusOrderMap[newStatus] || '';
    trade.update = Date.now();
    trade.save(function(err) {
        callback(err, trade);
    });
};

TradeHelper.notify = function(trade, callback) {
    // @formatter:off
    var subject = "[" + _getStatusName(trade.status) + "]" + 
        trade.itemSnapshot.name + "*" + trade.quantity + "=" + (trade.actualPrice ? trade.actualPrice : trade.expectedPrice);
        
    var content = 
        "交易：\n" + 
        JSON.stringify(trade, null, 4) + 
        "用户：\n" + 
        JSON.stringify(trade.peopleSnapshot, null, 4);
    // @formatter:on

    qsmail.send(subject, content, callback);
};

_getStatusName = function(status) {
    switch(status) {
        case 0:
            return "等待买家付款";
        case 1:
            return "等待倾秀代购";
        case 2:
            return "等待卖家发货";
        case 3:
            return "卖家已发货";
        case 4:
            return "买家已签收";
        case 5:
            return "交易成功";
        case 6:
            return "申请退货中";
        case 7:
            return "退货中";
        case 8:
            return "退款中";
        case 9:
            return "退款成功";
        case 10:
            return "退款失败";
    }
};

