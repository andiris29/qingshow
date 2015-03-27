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
var Http = require('http');

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
            if (req.body['pay']['weixin'] != null) {
                trade.pay = req.body.pay;
            }
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
                // Communicate to payment to get prepayid for weixin
                var orderName = '';
                trade.orders.forEach(function(element) {
                    orderName += element.itemSnapshot.name + ',';
                });
                if (orderName.length > 0) {
                    orderName = orderName.substring(0, orderName.length - 1);
                }

                var url = 'http://localhost:8080/payment/wechat/prepay?id=' + trade._id.toString() + '&totalFee=' + trade.totalFee + '&orderName=' + orderName;
                http.get(url, function(response) {
                    response.setEncoding('utf8');
                    response.on('data', function (chunk) {
                        var jsonObject = JSON.parse(chunk);
                        if (jsonObject.metadata) {
                            callback(jsonObject.metadata, trade, relationship);
                        } else {
                            trade.pay.weixin['prepayid'] = jsonObject.data.prepayid;
                            trade.save(function(err) {
                                callback(err, trade, relationship);
                            });
                        }
                    });

                }).on('error', function(error) {
                    callback(error.message, trade, relationship);
                });
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
                // Save the parameters from payment server.
                // handle at callback interface
                callback(ServerError.TradeStatusChangeError);
            } else if (newStatus == 2) {
                trade.agent = trade.agent || {};
                trade.agent.taobaoUserNick = param['agent']['taobaoUserNick'];
                trade.agent.taobaoTradeId = param['agent']['taobaoTradeId'];
            } else if (newStatus == 3) {
                trade.logistic = trade.logistic || {};
                trade.logistic.company = param['logistic']['company'];
                trade.logistic.trackingId = param['logistic']['trackingId'];
                // when use wechat pay, send deliver notify to wechat pay server
                if (trade.pay.weixin.prepayid  != null) {
                    var payInfo = trade.pay.weixin;
                    var url = 'http://localhost:8080/payment/wechat/deliverNotify?openid=' + payInfo.OpenId + '&transid=' + payInfo.transaction_id + '&out_trade_no=' + trade._id + '&deliver_status=1&deliver_msg=OK';
                    http.get(url, function(response) {
                        response.setEncoding('utf8');
                        response.on('data', function(chunk) {
                            var jsonObject = JSON.parse(chunk);
                            if (jsonObject.metadata) {
                                callback(jsonObject.metadata, trade);
                                return;
                            } else {
                                if (jsonObject.data.errcode != '0') {
                                    callback(jsonObject.data.errmsg, trade);
                                    return;
                                } 
                            }
                        });
                    }).on('error', function(error) {
                        callback(error, trade);
                        return;
                    })；
                }
            } else if (newStatus == 4) {
                trade.logistic = trade.logistic || {};
                trade.logistic.receiptDate = param['logistic']['receiptDate'];
            } else if (newStatus == 5) {
                // handle at callback interface
                callback(ServerError.TradeStatusChangeError);
            } else if (newStatus == 7) {
                trade.returnLogistic = trade.returnLogistic || {};
                trade.returnLogistic.company = param['returnLogistic']['company'];
                trade.returnLogistic.trackingId = param['returnLogistic']['trackingId'];
            } else if (newStatus == 8) {
                // TODO Communicate with payment server to request refund. [weixin]
            } else if (newStatus == 9) {
                // Save the parameters from payment server.
                // handle at callback interface
                callback(ServerError.TradeStatusChangeError);
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
        },
        function(trade, callback) {
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

trade.alipayCallback = {
    'method' : 'post',
    'func' : function(req, res) {
        async.waterfall([
        function(callback) {
            // get trade
            Trade.findOne({
                '_id' : RequestHelper.parseId(req.body.out_trade _no);
            }).exec(function(error, trade) {
                if (!error && !trade) {
                    callback(ServerError.TradeNotExist);
                } else if (error) {
                    callback(error);
                } else {
                    callback(null, trade);
                }
            });
        },
        function(trade, callback) {
            // Validate status
            var valid = _statusValidationMap[req.body.status];
            if (valid && valid.indexOf(trade.status) !== -1) {
                callback(null, trade);
            } else {
                callback(ServerError.TradeStatusChangeError);
            }
        },
        function(trade, callback){
            var newStatus = req.body.status;
            if (newStatus != 1 || newStatus != 5 || newStatus !=9 ) {
                callback(ServerError.TradeStatusChangeError);
            }
            trade.pay.alipay['trade_no'] = req.body['trade_no'];
            trade.pay.alipay['trade_status'] = req.body['trade_status'];
            trade.pay.alipay['total_fee'] = req.body['total_fee'];
            trade.pay.alipay['refund_status'] = req.body['refund_status'];
            trade.pay.alipay['gmt_refund'] = req.body['gmt_refund'];
            trade.pay.alipay['seller_id'] = req.body['seller_id'];
            trade.pay.alipay['seller_email'] = req.body['seller_email'];
            trade.pay.alipay['buyer_id'] = req.body['buyer_id'];
            trade.pay.alipay['buyer_email'] = req.body['buyer_email'];

            trade.pay.alipay.notifyLogs = trade.pay.alipay.notifyLogs || [];
            trade.pay.alipay.notifyLogs.push({
                'notify_type' : req.body['notify_type'],
                'notify_id' : req.body['notify_id'],
                'trade_status' : req.body['trade_status'],
                'refund_status' : req.body['refund_status'],
                'date' : Date.now
            });
            trade.save(function(error, trade) {
                callback(error, trade)
            });
        },
        function(trade, callback) {
            // update status
            var newStatus = param.status;
            TradeHelper.updateStatus(trade, newStatus, null, function(err, trade) {
                callback(err, trade);
            });
        },
        function(trade, callback) {
            // Send notification mail
            TradeHelper.notify(trade, function(err, info) {
                callback(err, trade);
            });
        }],
        function(error, trade) {
            ResponseHelper.response(res, error, {
                'trade' : trade
            });
        });
    }
};

trade.wechatCallback = {
    'method' : 'post',
    'func' : function(req, res) {
        async.waterfall([
        function(callback) {
            Trade.findOne({
                '_id' : RequestHelper.parseId(req.body.out_trade_no);
            }).exec(function(error, trade) {
                if (!error && !trade) {
                    callback(ServerError.TradeNotExist);
                } else if (error) {
                    callback(error);
                } else {
                    callback(null, trade);
                }
            });
        },
        function(trade, callback) {
            // Validate status
            var valid = _statusValidationMap[req.body.status];
            if (valid && valid.indexOf(trade.status) !== -1) {
                callback(null, trade);
            } else {
                callback(ServerError.TradeStatusChangeError);
            }
        },
        function(trade, callback) {
            var newStatus = req.body.status;
            if (newStatus != 1) {
                callback(ServerError.TradeStatusChangeError);
                return;
            }
            trade.pay.weixin['trade_mode'] = req.body['trade_mode'];
            trade.pay.weixin['partner'] = req.body['partner'];
            trade.pay.weixin['total_fee'] = req.body['total_fee'];
            trade.pay.weixin['transaction_id'] = req.body['transaction_id'];
            trade.pay.weixin['time_end'] = req.body['time_end'];
            trade.pay.weixin['AppId'] = req.body['AppId'];
            trade.pay.weixin['OpenId'] = req.body['OpenId'];
            //trade.pay.weixin[''] = req.body['trade_status'];
            //trade.pay.weixin[''] = req.body['bank_type'];
            //trade.pay.weixin[''] = req.body['bank_billno'];
            //trade.pay.weixin[''] = req.body['notify_id'];
            //trade.pay.weixin[''] = req.body['out_trade_no'];
            //trade.pay.weixin[''] = req.body['attach'];
            //trade.pay.weixin[''] = req.body['transport_fee'];
            //trade.pay.weixin[''] = req.body['product_fee'];
            //trade.pay.weixin[''] = req.body['discount'];
            trade.pay.weixin.notifyLogs = trade.pay.weixin.notifyLogs || [];
            trade.pay.weixin.notifyLogs.push({
                'notify_id' : req.body['notify_id'],
                'trade_state' : req.body['trade_state'],
                'date' : Date.now
            });
            trade.save(function(error, trade) {
                callback(error, trade);
            });
        }
        function(trade, callback) {
            // update status
            var newStatus = param.status;
            TradeHelper.updateStatus(trade, newStatus, null, function(err, trade) {
                callback(err, trade);
            });
        },
        function(trade, callback) {
            // Send notification mail
            TradeHelper.notify(trade, function(err, info) {
                callback(err, trade);
            });
        }],
        function(error, trade) {
            ResponseHelper.response(res, error, {
                'trade' : trade
            });
        });
    }
};

trade.refreshPaymentStatus = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        async.waterfall([
        function(callback) {
            // get trade
            Trade.findOne({
                '_id' : RequestHelper.parseId(req.body._id);
            }).exec(function(error, trade) {
                if (!error && !trade) {
                    callback(ServerError.TradeNotExist);
                } else if (error) {
                    callback(error);
                } else {
                    callback(null, trade);
                }
            });
        },
        function(trade, callback) {
            if(!trade.pay.alipay.trade_no) {
                callback(null, trade);
            } else {
                // pay with wechat
                var url = 'http://localhost:8080/payment/wechat/queryOrder?id=' + trade._id.toString;
                http.get(url, function(response) {
                    response.setEncoding('utf8');
                    response.on('data', function(chunk) {
                        var jsonObject = JSON.parse(chunk);
                        if (jsonObject.metadata) {
                            callback(jsonObject.metadata, trade);
                        } else {
                            if (jsonObject.data.errcode != '0') {
                                callback(jsonObject.data.errmsg, trade);
                                return;
                            }
                            var orderInfo = jsonObject.data.order_info;
                            trade.pay.weixin['trade_mode'] = orderInfo['trade_mode'];
                            trade.pay.weixin['partner'] = orderInfo['partner'];
                            trade.pay.weixin['total_fee'] = orderInfo['total_fee'];
                            trade.pay.weixin['transaction_id'] = orderInfo['transaction_id'];
                            trade.pay.weixin['time_end'] = orderInfo['time_end'];
                            trade.pay.weixin['AppId'] = orderInfo['AppId'];
                            trade.pay.weixin['OpenId'] = orderInfo['OpenId'];
                            
                            trade.pay.weixin.notifyLogs = trade.pay.weixin.notifyLogs || [];
                            if (trade.pay.weixin.notifyLogs.length > 0) {
                                trade.pay.weixin.notifyLogs[trade.pay.weixin.notifyLogs.length - 1].trade_state = orderInfo['trade_state'];
                            }

                            trade.save(function(error, trade) {
                                callback(error, trade);
                            });
                        }
                    });
                }).on('error', function(error) {
                    callback(error, trade);
                });
            }
        }],
        function(error, trade) {
            ResponseHelper.response(res, error, {
                'trade' : trade
            });
        });
    }
}
