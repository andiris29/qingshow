var mongoose = require('mongoose');
var async = require('async');
var _ = require('underscore');

var Trade = require('../../model/trades');
var People = require('../../model/peoples');
var Item = require('../../model/items');
var RPeopleShareTrade = require('../../model/rPeopleShareTrade');
var jPushAudiences = require('../../model/jPushAudiences');

var RequestHelper = require('../helpers/RequestHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var TradeHelper = require('../helpers/TradeHelper');
var RelationshipHelper = require('../helpers/RelationshipHelper');
var MongoHelper = require('../helpers/MongoHelper');
var ContextHelper = require('../helpers/ContextHelper');

var ServerError = require('../server-error');
var request = require('request');

var PushNotificationHelper = require('../helpers/PushNotificationHelper');

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
            trade.ownerRef = req.qsCurrentUserId;
            trade.peopleSnapshot = people;
            trade.shareToPay = true;
            trade.quantity = req.body.quantity;
            trade.expectedPrice = req.body.expectedPrice;
            trade.itemSnapshot = req.body.itemSnapshot;
            trade.selectedSkuProperties = req.body.selectedSkuProperties;
            trade.itemRef = RequestHelper.parseId(req.body.itemSnapshot._id);
            trade.save(function(err) {
                callback(err, trade);
            });
        },
        function(trade, callback) {
            // Update trade status
            TradeHelper.updateStatus(trade, 0, null, req.qsCurrentUserId, function(err) {
                callback(err, trade);
            });
        }], function(error, trade) {
            // Send response
            ResponseHelper.response(res, error, {
                'trade' : trade
            });
            if (!error) {
                TradeHelper.notify(trade);
            }
        });
    }
};

trade.prepay = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        async.waterfall([
        function(callback) {
            Trade.findOne({
                '_id' : RequestHelper.parseId(req.body._id)
            }, function(err, trade) {
                if (err) {
                    callback(err);
                } else if (!trade) {
                    callback(ServerError.TradeNotExist);
                } else {
                    callback(null, trade);
                }
            });
        },
        function(trade, callback) {
            trade.totalFee = Math.max(0.01, RequestHelper.parseNumber(req.body.totalFee)).toFixed(2);
            trade.selectedPeopleReceiverUuid = req.body.selectedPeopleReceiverUuid;
            trade.pay = {};

            trade.save(function(err, trade) {
                if (err) {
                    callback(err);
                } else {
                    callback(null, trade);
                }
            });
        },
        function(trade, callback) {
            if (req.body.pay && req.body.pay['weixin']) {
                trade.pay = req.body.pay;
                // Communicate to payment to get prepayid for weixin
                var orderName = trade.itemSnapshot.name;
                var url = 'http://localhost:8080/payment/wechat/prepay?id=' + trade._id.toString() + '&totalFee=' + trade.totalFee + '&orderName=' + encodeURIComponent(orderName) + '&clientIp=' + RequestHelper.getIp(req);
                request.get(url, function(error, response, body) {
                    var jsonObject = JSON.parse(body);
                    if (jsonObject.metadata) {
                        callback(jsonObject.metadata, trade);
                    } else {
                        trade.pay.weixin['prepayid'] = jsonObject.data.prepay_id;
                        trade.save(function(err) {
                            callback(err, trade);
                        });
                    }
                });
            } else {
                callback(null, trade);
            }
        }], function(err, trade) {
            // Send response
            ResponseHelper.response(res, err, {
                'trade' : trade
            });
        });
    }
};

// Validate new status
var _statusValidationMap = {
    1 : [0],
    2 : [1],
    3 : [2],
    5 : [3],
    7 : [3],
    9 : [7],
    10 : [7],
    15 : [3],
    17 : [0, 1, 2, 3, 4, 5, 6, 7, 9, 10, 15, 18],
    18 : [0, 1, 2]
};

var _validateStatus = function(trade, newStatus, callback) {
    // Validate status
    var valid = _statusValidationMap[newStatus];
    if (valid && valid.indexOf(trade.status) !== -1) {
        callback(null, trade);
    } else {
        callback(ServerError.TradeStatusChangeError);
    }
};

var _weixinDeliveryNotify = function(trade) {
    var payInfo = trade.pay.weixin;
    var url = 'http://localhost:8080/payment/wechat/deliverNotify?openid=' + payInfo.OpenId + '&transid=' + payInfo.transaction_id + '&out_trade_no=' + trade._id + '&deliver_status=1&deliver_msg=OK';
    request.get(url, function(error, response, body) {
        var jsonObject = JSON.parse(body);
        if (jsonObject.metadata) {
            callback(jsonObject.metadata, trade);
        } else {
            if (jsonObject.data.errcode != '0') {
                callback(jsonObject.data.errmsg, trade);
            } else {
                callback(null, trade);
            }
        }
    });
};

trade.statusTo = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        var param = req.body,
            newStatus = param.status;
        async.waterfall([
        function(callback) {
            // get trade;
            Trade.findOne({
                '_id' : RequestHelper.parseId(param._id)
            }, function(err, trade) {
                if (err) {
                    callback(err);
                } else if (!trade) {
                    callback(ServerError.TradeNotExist);
                } else {
                    callback(null, trade);
                }
            });
        },
        function(trade, callback) {
            _validateStatus(trade, newStatus, callback);
        },
        function(trade, callback) {
            // update trade
            if (newStatus == 1) {
                trade.actualPrice = req.body.actualPrice;
                trade.save(function(err, trade) {
                    callback(err, trade);
                    // Push Notification
                    if (trade._id.toString() != req.qsCurrentUserId) {
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
                                    'id' : param._id,
                                    'command' : PushNotificationHelper.CommandTradeInitialized
                                }, null);
                            }
                        });
                    }
                });
            } else if (newStatus == 2) {
                // Save the parameters from payment server.
                // handle at callback interface
                callback(ServerError.TradeStatusChangeError);
            } else if (newStatus == 3 ) {
                trade.logistic = trade.logistic || {};
                trade.logistic.company = param.logistic.company;
                trade.logistic.trackingId = param.logistic.trackingId;
                if (trade.pay.weixin.prepayid != null) {
                    _weixinDeliveryNotify(trade);
                }
                // Push Notification
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
                        PushNotificationHelper.push(targets, PushNotificationHelper.MessageTradeShipped, {
                            'id' : param._id,
                            'command' : PushNotificationHelper.CommandTradeShipped
                        }, null);
                    }
                });
                callback(null, trade);
            } else if (newStatus == 7) {
                trade.returnLogistic = trade.returnLogistic || {};
                trade.returnLogistic.company = param.returnLogistic.company;
                trade.returnLogistic.trackingId = param.returnLogistic.trackingId;
                callback(null, trade);
            } else {
                callback(null, trade);
            }
        },
        function(trade, callback) {
            TradeHelper.updateStatus(trade, newStatus, param.comment, req.qsCurrentUserId, function(err, trade) {
                callback(err, trade);
            });
        }], function(error, trade) {
            ResponseHelper.response(res, error, {
                'trade' : trade
            });
            if (!error) {
                TradeHelper.notify(trade);
            }
        });
    }
};

trade.queryCreatedBy = {
    'method' : 'get',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        var userId = req.queryString._id ? RequestHelper.parseId(req.queryString._id) : req.qsCurrentUserId;
        ServiceHelper.queryPaging(req, res, function(qsParam, callback) {
            var criteria = {
                'ownerRef' : userId
            };
            if (qsParam.inProgress === "true") {
                criteria.status = {
                    '$in' : [1, 2, 3, 7]
                };
            }
            MongoHelper.queryPaging(Trade.find(criteria).sort({'create' : -1}), Trade.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
        }, function(trades) {
            return {
                'trades' : trades 
            };
        }, {
            'afterQuery' : function (qsParam, currentPageModels, numTotal, callback) {
                // Append Context
                ContextHelper.appendTradeContext(req.qsCurrentUserId, currentPageModels, callback);
            }
        });
    }
};

trade.alipayCallback = {
    'method' : 'post',
    'func' : function(req, res) {
        var newStatus = 2;
        async.waterfall([
        function(callback) {
            Trade.findOne({
                '_id' : RequestHelper.parseId(req.body.out_trade_no)
            }, callback);
        },
        function(trade, callback) {
            _validateStatus(trade, newStatus, callback);
        },
        function(trade, callback) {
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
                //'date' : Date.now
            });
            callback(null, trade);
        },
        function(trade, callback) {
            TradeHelper.updateStatus(trade, newStatus, null, null, callback);
        }], function(error, trade) {
            ResponseHelper.response(res, error, {
                'trade' : trade
            });
            if (!error) {
                TradeHelper.notify(trade);
            }
        });
    }
};

trade.wechatCallback = {
    'method' : 'post',
    'func' : function(req, res) {
        var newStatus = 2;
        async.waterfall([
        function(callback) {
            Trade.findOne({
                '_id' : RequestHelper.parseId(req.body.out_trade_no)
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
            _validateStatus(trade, newStatus, callback);
        },
        function(trade, callback) {
            trade.pay.weixin['trade_mode'] = req.body['trade_type'];
            trade.pay.weixin['partner'] = req.body['mch_id'];
            trade.pay.weixin['total_fee'] = req.body['total_fee'] / 100;
            trade.pay.weixin['transaction_id'] = req.body['transaction_id'];
            trade.pay.weixin['time_end'] = req.body['time_end'];
            trade.pay.weixin['appId'] = req.body['appid'];
            trade.pay.weixin['openId'] = req.body['openid'];
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
                //'trade_state' : req.body['trade_state'],
                //'date' : Date.now
            });
            callback(null, trade);
        },
        function(trade, callback) {
            TradeHelper.updateStatus(trade, newStatus, null, null, callback);
        }], function(error, trade) {
            if (error === 'pass') {
                error = null;
            }
            ResponseHelper.response(res, error, {
                'trade' : trade
            });
            if (!error) {
                TradeHelper.notify(trade);
            }
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
                '_id' : RequestHelper.parseId(req.body._id)
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
            if (!trade.pay.alipay.trade_no) {
                callback(null, trade);
            } else {
                // pay with wechat
                var url = 'http://localhost:8080/payment/wechat/queryOrder?id=' + trade._id.toString;
                request.get(url, function(error, response, body) {
                    var jsonObject = JSON.parse(body);
                    if (jsonObject.metadata) {
                        callback(jsonObject.metadata, trade);
                    } else {
                        var orderInfo = jsonObject.data;
                        trade.pay.weixin['trade_mode'] = orderInfo['trade_type'];
                        trade.pay.weixin['partner'] = orderInfo['mch_id'];
                        trade.pay.weixin['total_fee'] = orderInfo['total_fee'];
                        trade.pay.weixin['fee_type'] = orderInfo['fee_type'];
                        trade.pay.weixin['transaction_id'] = orderInfo['transaction_id'];
                        trade.pay.weixin['time_end'] = orderInfo['time_end'];
                        //trade.pay.weixin['AppId'] = orderInfo['appid'];
                        trade.pay.weixin['OpenId'] = orderInfo['openId'];

                        trade.pay.weixin.notifyLogs = trade.pay.weixin.notifyLogs || [];
                        if (trade.pay.weixin.notifyLogs.length > 0) {
                            trade.pay.weixin.notifyLogs[trade.pay.weixin.notifyLogs.length - 1].trade_state = orderInfo['trade_state'];
                        }

                        trade.save(function(error, trade) {
                            callback(error, trade);
                        });
                    }
                });
            }
        }], function(error, trade) {
            ResponseHelper.response(res, error, {
                'trade' : trade
            });
        });
    }
};

trade.share = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        var targetRef, initiatorRef;
        async.waterfall([
        function(callback) {
            try {
                var param = req.body;
                targetRef = RequestHelper.parseId(param._id);
                initiatorRef = req.qsCurrentUserId;
            } catch (err) {
                callback(err);
            }
            callback();
        },
        function(callback) {
            // Share
            RelationshipHelper.create(RPeopleShareTrade, initiatorRef, targetRef, function(err, relationship) {
                callback(err);
            });
        }], function(err) {
            ResponseHelper.response(res, err);
        });

    }
};

trade.query = {
    'method' : 'get',
    'func' : function(req, res) {
        ServiceHelper.queryPaging(req, res, function(qsParam, callback) {
            var criteria = {};
            if (qsParam._ids || qsParam._ids.length > 0) {
                criteria._id = {
                    '$in' : RequestHelper.parseIds(qsParam._ids)
                };
            }
            MongoHelper.queryPaging(Trade.find(criteria), Trade.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
        }, function(trades) {
            return {
                'trades' : trades 
            };
        }, {
            'afterQuery' : function (qsParam, currentPageModels, numTotal, callback) {
                async.series([
                function(callback) {
                    Item.populate(currentPageModels, {
                        'path' : 'itemRef',
                        'model' : 'items'
                    }, callback);
                },
                function(callback) {
                    // Append Context
                    ContextHelper.appendTradeContext(req.qsCurrentUserId, currentPageModels, callback);
                }], callback);
            }
        });
    }
};

trade.queryByPhase = {
    'method' : 'get',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        ServiceHelper.queryPaging(req, res, function(qsParam, callback) {
            var criteria = {};
            if (qsParam.phases|| qsParam.phases.length > 0) {
                criteria.phase = {
                    '$in' : RequestHelper.parseNumbers(qsParam.phases)
                }
            }
            criteria.ownerRef = req.qsCurrentUserId;
            MongoHelper.queryPaging(Trade.find(criteria).sort({'phase' : 1}), Trade.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
        }, function(trades) {
            return {
                'trades' : trades 
            };
        }, {
            'afterQuery' : function (qsParam, currentPageModels, numTotal, callback) {
                async.series([
                function(callback) {
                    Item.populate(currentPageModels, {
                        'path' : 'itemRef',
                        'model' : 'items'
                    }, callback);
                },
                function(callback) {
                    // Append Context
                    ContextHelper.appendTradeContext(req.qsCurrentUserId, currentPageModels, callback);
                }], callback);
            }
        });
    }
};
