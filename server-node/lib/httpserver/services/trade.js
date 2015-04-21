var mongoose = require('mongoose');
var async = require('async');

var Trade = require('../../model/trades');
var People = require('../../model/peoples');
var Item = require('../../model/items');
var rPeopleCreateTrade = require('../../model/rPeopleCreateTrade');

var RequestHelper = require('../helpers/RequestHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var TradeHelper = require('../helpers/TradeHelper');
var RelationshipHelper = require('../helpers/RelationshipHelper');

var ServerError = require('../server-error');

var trade = module.exports;

trade.create = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        async.waterfall([
        function(callback) {
            // Find login people
            People.findOne({
                '_id' : req.qsCurrentUserId
            }, callback);
        },
        function(people, callback) {
            // Save trade
            var trade = new Trade();
            trade.orders = [];
            req.body.orders.forEach(function(element) {
                trade.orders.push({
                    'quantity' : element.quantity,
                    'price' : element.price,
                    'itemSnapshot' : element.itemSnapshot,
                    'peopleSnapshot' : people,
                    'selectedItemSkuId' : element.selectedItemSkuId,
                    'selectedPeopleReceiverUuid' : element.selectedPeopleReceiverUuid
                });
            });
            trade.totalFee = req.body.totalFee;
            trade.save(function(err) {
                callback(err, trade);
            });
        },
        function(trade, callback) {
            // Make relationship
            var initiatorRef = req.qsCurrentUserId;
            var targetRef = trade._id;
            RelationshipHelper.create(rPeopleCreateTrade, initiatorRef, targetRef, function(err, relationship) {
                callback(err, trade, relationship);
            });
        },
        function(trade, relationship, callback) {
            // Update trade status
            TradeHelper.updateStatus(trade, 0, req.qsCurrentUserId, function(err) {
                callback(err, trade, relationship);
            });
        },
        function(trade, relationship, callback) {
            if (trade.pay && trade.pay.weixin) {
                // TODO Communicate to payment to get prepayid for weixin
                callback(null, trade, relationship);
            } else {
                callback(null, trade, relationship);
            }
        }], function(error, trade, relationship) {
            // Send response
            ResponseHelper.response(res, error, {
                'trade' : trade,
                'rPeopleCreateTrade' : relationship
            });
            // Send notification mail
            TradeHelper.notify(trade);
        });
    }
};

// Validate new status
var _statusValidationMap = {
    1 : [0], // 等待买家付款
    2 : [1], // 等待倾秀代购
    3 : [2], // 等待卖家发货
    4 : [3], // 买家已签收
    5 : [3, 4], // 交易成功
    6 : [1, 2, 3, 4], // 申请退货中
    7 : [6], // 退货中
    8 : [7], // 退款中
    9 : [8], // 退款成功
    10 : [8],// 退款失败
};
trade.statusTo = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        var param;
        param = req.body;
        async.waterfall([
        function(callback) {
            // get trade;
            Trade.findOne({
                '_id' : RequestHelper.parseId(param._id)
            }).exec(function(error, trade) {
                if (!error && !trade) {
                    callback(ServerError.TradeNotExist);
                }
                if (error) {
                    callback(error);
                } else {
                    callback(null, trade);
                }
            });
        },
        function(trade, callback) {
            // Validate status
            var valid = _statusValidationMap[param.status];
            if (valid && valid.indexOf(trade.status) !== -1) {
                callback(null, trade);
            } else {
                callback(ServerError.TradeStatusChangeError);
            }

        },
        function(trade, callback) {
            // update trade
            var newStatus = param.status;
            if (newStatus == 1) {
                // TODO Save the parameters from payment server.
            } else if (newStatus == 2) {
                trade.agent = trade.agent || {};
                trade.agent.taobaoUserNick = param['agent']['taobaoUserNick'];
                trade.agent.taobaoTradeId = param['agent']['taobaoTradeId'];
            } else if (newStatus == 3) {
                trade.logistic = trade.logistic || {};
                trade.logistic.company = param['logistic']['company'];
                trade.logistic.trackingId = param['logistic']['trackingId'];
            } else if (newStatus == 4) {
                trade.logistic = trade.logistic || {};
                trade.logistic.receiptDate = param['logistic']['receiptDate'];
            } else if (newStatus == 7) {
                trade.returnLogistic = trade.returnLogistic || {};
                trade.returnLogistic.company = param['returnLogistic']['company'];
                trade.returnLogistic.trackingId = param['returnLogistic']['trackingId'];
            } else if (newStatus == 8) {
                // TODO Communicate with payment server to request refund.
            } else if (newStatus == 9) {
                // TODO Save the parameters from payment server.
            } else if (newStatus == 10) {
                // TODO Save the parameters from payment server.
            }
            //trade.status = newStatus;
            trade.save(function(error, trade) {
                callback(error, trade);
            });
        },
        function(trade, callback) {
            // update status
            var newStatus = param.status;
            TradeHelper.updateStatus(trade, newStatus, req.qsCurrentUserId, function(err, trade) {
                callback(err, trade);
            });
        },
        function(trade, callback) {
            People.findOne({
                '_id' : req.qsCurrentUserId
            }).exec(function(error, people) {
                callback(error, trade, people);
            });
        },
        function(trade, people, callback) {
            // Send notification mail
            TradeHelper.notify(trade, function(err, info) {
                callback(err, trade);
            });
        }], function(error, trade) {
            ResponseHelper.response(res, error, {
                'trade' : trade
            });
        });
    }
};

trade.queryCreatedBy = {
    'method' : 'get',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        ServiceHelper.queryRelatedTrades(req, res, rPeopleCreateTrade, {
            'query' : 'initiatorRef',
            'result' : 'targetRef'
        });
    }
};

