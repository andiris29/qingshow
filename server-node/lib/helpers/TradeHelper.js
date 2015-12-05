var mongoose = require('mongoose');
var async = require('async');

var qsmail = require('../runtime/').mail;
var winston = require('winston');
var RequestHelper = require('./RequestHelper');
var NotificationHelper = require('./NotificationHelper');

var TradeHelper = module.exports;

var _statusOrderMap = {
    0 : '01',
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

TradeHelper.updateStatus = function(trade, newStatus, peopleId, callback) {
    var oldStatus = trade.status;

    if (newStatus === 2 && !trade.pay.forge) {
        trade.pay.create = Date.now();
    }

    if (newStatus === 7 || newStatus === 17) {
        trade.highlight = null;
    }

    if (newStatus === 5 || newStatus === 15 || newStatus === 7) {
        NotificationHelper.read([trade.ownerRef], {
            'extra.command' : NotificationHelper.CommandTradeShipped,
            'extra._id' : trade._id
        }, function(err){});
    }


    trade.status = newStatus;
    trade.statusOrder = _statusOrderMap[newStatus];
    trade.update = Date.now();
    trade.save(function(err) {
        callback(err, trade);
    });
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

