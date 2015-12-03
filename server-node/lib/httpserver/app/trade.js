var async = require('async'),
    _ = require('underscore'),
    request = require('request'),
    winston = require('winston');

var Trade = require('../../dbmodels').Trade,
    TradeCode = require('../../dbmodels').TradeCode,
    People = require('../../dbmodels').People,
    Item = require('../../dbmodels').Item;

var RequestHelper = require('../../helpers/RequestHelper'),
    ResponseHelper = require('../../helpers/ResponseHelper'),
    TradeHelper = require('../../helpers/TradeHelper'),
    RelationshipHelper = require('../../helpers/RelationshipHelper'),
    MongoHelper = require('../../helpers/MongoHelper'),
    ContextHelper = require('../../helpers/ContextHelper'),
    BonusHelper = require('../../helpers/BonusHelper'),
    TraceHelper = require('../../helpers/TraceHelper'),
    NotificationHelper = require('../../helpers/NotificationHelper');

var errors = require('../../errors');

var trade = module.exports;

trade.create = {
    'method' : 'post',
    'func' : [
        require('../middleware/injectCurrentUser'),
        require('../middleware/validateLoginAsUser'),
        require('../middleware/injectModelGenerator').generateInjectOneByObjectId(Item, 'itemRef'),
        function(req, res, next) {
            // Save trade
            var trade = new Trade();
            trade.status = TradeCode.STATUS_WAITING_PAY;
            trade.ownerRef = req.qsCurrentUserId;
            trade.quantity = req.body.quantity;
            trade.itemSnapshot = req.injection.itemRef.toJSON();
            trade.totalFee = trade.quantity * (trade.itemSnapshot.promoPrice * 100 - trade.itemSnapshot.expectable.reduction * 100) / 100;
            trade.selectedSkuProperties = req.body.selectedSkuProperties;
            trade.itemRef = req.injection.itemRef._id;
            if (req.body.promoterRef) {
                trade.promoterRef = RequestHelper.parseId(req.body.promoterRef);
            }
            trade.save(function(err) {
                if (err) {
                    next(errors.genUnkownError(err));
                } else {
                    ResponseHelper.writeData(res, {'trade' : trade});
                    
                    TraceHelper.trace('behavior-trade-creation', req, {
                        '_tradeId' : trade._id.toString(),
                        'selectedSkuProperties' : trade.selectedSkuProperties
                    });
                    next();
                }
            });
        }
    ]
};

trade.prepay = {
    'method' : 'post',
    'func' : [
        require('../middleware/injectCurrentUser'),
        require('../middleware/validateLoginAsUser'),
        require('../middleware/injectModelGenerator').generateInjectOneByObjectId(Trade, '_id', 'tradeRef'),
        function(req, res, next) {
            // Save receiver
            req.injection.qsCurrentUser.receivers.forEach(function(receiver) {
                if (receiver.uuid === req.body.selectedPeopleReceiverUuid) {
                    req.injection.tradeRef.receiver = receiver;
                }
            });
            next();
        },
        function(req, res, next) {
            var trade = req.injection.tradeRef;
            
            if (req.body.pay && req.body.pay['weixin']) {
                trade.pay = req.body.pay;
                // Communicate to payment to get prepayid for weixin
                var orderName = trade.itemSnapshot.name;
                var url = global.qsConfig.payment.url + '/payment/wechat/prepay' + 
                    '?id=' + trade._id.toString() + 
                    '&totalFee=' + trade.totalFee + 
                    '&orderName=' + encodeURIComponent(orderName) + 
                    '&clientIp=' + RequestHelper.getIp(req) + 
                    '&notifyUrl=' + encodeURIComponent(global.qsConfig.paymentServiceRoot + '/wechat/callback');
                request.get(url, function(error, response, body) {
                    var jsonObject;
                    try {
                        jsonObject = JSON.parse(body);
                        TraceHelper.trace('pay-integration-callback', req, _.extend(jsonObject, {
                            _tradeId : trade._id
                        }));
                    } catch (err) {
                        winston.error('wechat/prepay failed.');
                        winston.error('url = ' + url);
                        winston.error('body = ' + body);
                        throw err;
                    }
                    if (jsonObject.metadata) {
                        next(errors.genUnkownError(jsonObject.metadata));
                    } else {
                        trade.pay.weixin['prepayid'] = jsonObject.data.prepay_id;
                        trade.save(function(err) {
                            if (err) {
                                next(errors.genUnkownError(err));
                            } else {
                                ResponseHelper.writeData(res, {'trade' : trade});
                                next();
                            }
                        });
                    }
                });
            } else {
                ResponseHelper.writeData(res, {'trade' : trade});
                next();
            }
        }
    ]
};

var _weixinDeliveryNotify = function(trade) {
    var payInfo = trade.pay.weixin;
    var url = global.qsConfig.payment.url + '/payment/wechat/deliverNotify?openid=' + payInfo.OpenId + '&transid=' + payInfo.transaction_id + '&out_trade_no=' + trade._id + '&deliver_status=1&deliver_msg=OK';
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

var _generateStatusValidator = function(validStatuses) {
    return function(req, res, next) {
        if (validStatuses.indexOf(req.injection.tradeRef.status) !== -1) {
            next();
        } else {
            next(errors.TradeStatusChangeError);
        }
    };
};

var _generateStatusUpdater = function(newStatus) {
    return function(req, res, next) {
        var trade = req.injection.tradeRef,
            oldStatus = trade.status;
        TradeHelper.updateStatus(trade, newStatus, req.qsCurrentUserId, function(err, trade) {
            if (err) {
                next(errors.genUnkownError(err));
            } else {
                TraceHelper.trace('trade-process', req, 
                    {'_tradeId' : trade._id.toString(), 'oldStatus' : oldStatus, 'newStatus' : newStatus});
                    
                ResponseHelper.writeData(res, {'trade' : trade});
                next();
            }
        });
    };
};

trade.deliver = {
    'method' : 'post', 
    'func' : [
        require('../middleware/injectCurrentUser'),
        require('../middleware/validateLoginAsUser'),
        require('../middleware/injectModelGenerator').generateInjectOneByObjectId(Trade, '_id', 'tradeRef'),
        _generateStatusValidator([TradeCode.STATUS_PAID]),
        function(req, res, next) {
            var param = req.body,
                trade = req.injection.tradeRef;
            // Save logistic
            trade.logistic = trade.logistic || {};
            trade.logistic.company = param.logistic.company;
            trade.logistic.trackingId = param.logistic.trackingId;
            // Notify weixin
            if (trade.pay.weixin && trade.pay.weixin.prepayid) {
                _weixinDeliveryNotify(trade);
            }
            // Push Notification
            NotificationHelper.notify([trade.ownerRef], NotificationHelper.MessageTradeShipped, {
                '_id' : RequestHelper.parseId(param._id),
                'command' : NotificationHelper.CommandTradeShipped
            }, null);
            
            next();
        },
        _generateStatusUpdater(TradeCode.STATUS_DELIVERED)
    ]
};

trade.return = {
    'method' : 'post', 
    'func' : [
        require('../middleware/injectCurrentUser'),
        require('../middleware/validateLoginAsUser'),
        require('../middleware/injectModelGenerator').generateInjectOneByObjectId(Trade, '_id', 'tradeRef'),
        _generateStatusValidator([TradeCode.STATUS_DELIVERED]),
        function(req, res, next) {
            var param = req.body,
                trade = req.injection.tradeRef;
            // Save returnLogistic
            trade.note = param.note;
            trade.returnLogistic = trade.returnLogistic || {};
            trade.returnLogistic.company = param.returnLogistic.company;
            trade.returnLogistic.trackingId = param.returnLogistic.trackingId;
            
            next();
        },
        _generateStatusUpdater(TradeCode.STATUS_RETURN)
    ]
};

trade.returnComplete = {
    'method' : 'post', 
    'func' : [
        require('../middleware/injectCurrentUser'),
        require('../middleware/validateLoginAsUser'),
        require('../middleware/injectModelGenerator').generateInjectOneByObjectId(Trade, '_id', 'tradeRef'),
        _generateStatusValidator([TradeCode.STATUS_RETURN]),
        function(req, res, next) {
            req.injection.tradeRef.adminNote = req.body.adminNote;
            // Push Notification
            NotificationHelper._push([trade.ownerRef], NotificationHelper.MessageTradeRefundComplete, {
                '_id' : req.injection.tradeRef._id,
                'command' : NotificationHelper.CommandTradeRefundComplete
            }, null);
            
            next();
        },
        _generateStatusUpdater(TradeCode.STATUS_RETURN_COMPLETE)
    ]
};

trade.returnFailed = {
    'method' : 'post', 
    'func' : [
        require('../middleware/injectCurrentUser'),
        require('../middleware/validateLoginAsUser'),
        require('../middleware/injectModelGenerator').generateInjectOneByObjectId(Trade, '_id', 'tradeRef'),
        _generateStatusValidator([TradeCode.STATUS_RETURN]),
        function(req, res, next) {
            req.injection.tradeRef.adminNote = req.body.adminNote;
            next();
        },
        _generateStatusUpdater(TradeCode.STATUS_RETURN_FAILED)
    ]
};

trade.cancel = {
    'method' : 'post', 
    'func' : [
        require('../middleware/injectCurrentUser'),
        require('../middleware/validateLoginAsUser'),
        require('../middleware/injectModelGenerator').generateInjectOneByObjectId(Trade, '_id', 'tradeRef'),
        function(req, res, next) {
            req.injection.tradeRef.adminNote = req.body.adminNote;
            next();
        },
        _generateStatusUpdater(TradeCode.STATUS_CANCEL)
    ]
};

trade.alipayCallback = {
    'method' : 'post',
    'func' : [
        require('../middleware/injectModelGenerator').generateInjectOneByObjectId(Trade, 'out_trade_no', 'tradeRef'),
        _generateStatusValidator([TradeCode.STATUS_WAITING_PAY]),
        function(req, res, next) {
            var trade = req.injection.tradeRef;
            
            TraceHelper.trace('pay-integration-callback', req, _.extend(req.body, {
                _tradeId : trade._id
            }));
            
            trade.highlight = Date.now();
            trade.pay.alipay['trade_no'] = req.body['trade_no'];
            trade.pay.alipay['trade_status'] = req.body['trade_status'];
            trade.pay.alipay['total_fee'] = req.body['total_fee'];
            trade.pay.alipay['refund_status'] = req.body['refund_status'];
            trade.pay.alipay['gmt_refund'] = req.body['gmt_refund'];
            trade.pay.alipay['seller_id'] = req.body['seller_id'];
            trade.pay.alipay['seller_email'] = req.body['seller_email'];
            trade.pay.alipay['buyer_id'] = req.body['buyer_id'];
            trade.pay.alipay['buyer_email'] = req.body['buyer_email'];
            next();
        },
        _generateStatusUpdater(TradeCode.STATUS_PAID),
        function(req, res, next) {
            BonusHelper.createTradeBonus(req.injection.tradeRef, next);
        }
    ]
};

trade.wechatCallback = {
    'method' : 'post',
    'func' : [
        require('../middleware/injectModelGenerator').generateInjectOneByObjectId(Trade, 'out_trade_no', 'tradeRef'),
        _generateStatusValidator([TradeCode.STATUS_WAITING_PAY]),
        function(req, res, next) {
            var trade = req.injection.tradeRef;
            
            TraceHelper.trace('pay-integration-callback', req, _.extend(req.body, {
                _tradeId : trade._id
            }));
            
            trade.highlight = Date.now();
            trade.pay.weixin['trade_mode'] = req.body['trade_type'];
            trade.pay.weixin['partner'] = req.body['mch_id'];
            trade.pay.weixin['total_fee'] = req.body['total_fee'] / 100;
            trade.pay.weixin['transaction_id'] = req.body['transaction_id'];
            trade.pay.weixin['time_end'] = req.body['time_end'];
            trade.pay.weixin['appId'] = req.body['appid'];
            trade.pay.weixin['openId'] = req.body['openid'];
            
            next();
        },
        _generateStatusUpdater(TradeCode.STATUS_PAID),
        function(req, res, next) {
            BonusHelper.createTradeBonus(req.injection.tradeRef, next);
        }
    ]
};


trade.postpay = {
    'method' : 'post',
    'permissionValidators' : ['roleUserValidator'],
    'func' : function(req, res) {
        async.waterfall([
        function(callback) {
            // get trade
            Trade.findOne({
                '_id' : RequestHelper.parseId(req.body._id)
            }).exec(function(error, trade) {
                if (!error && !trade) {
                    callback(errors.TradeNotExist);
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
                var url = global.qsConfig.payment.url + '/payment/wechat/queryOrder?id=' + trade._id.toString;
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

trade.query = {
    'method' : 'get',
    'permissionValidators' : ['roleUserValidator'],
    'func' : function(req, res) {
        ServiceHelper.queryPaging(req, res, function(qsParam, callback) {
            var criteria = {};
            if (qsParam._ids || qsParam._ids.length > 0) {
                criteria._id = {
                    '$in' : RequestHelper.parseIds(qsParam._ids)
                };
            }
            MongoHelper.queryPaging(Trade.find(criteria).populate('itemRef').populate('ownerRef'), Trade.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
        }, function(trades) {
            return {
                'trades' : trades 
            };
        }, {
            'afterQuery' : function (qsParam, currentPageModels, numTotal, callback) {
                Trade.populate(currentPageModels, {
                    'path' : 'itemRef.shopRef',
                    'model' : 'peoples'
                }, function() {
                    ContextHelper.appendTradeContext(req.qsCurrentUserId, currentPageModels, callback); 
                });
            }
        });
    }
};

trade.queryHighlighted = {
    'method' : 'get',
    'func' : function (req, res){
        ServiceHelper.queryPaging(req, res, function(qsParam, callback){
            var criteria = {
                'highlight' : {
                    '$ne' : null
                }
            };
            MongoHelper.queryPaging(Trade.find(criteria).sort({'highlight' : -1}).populate('itemRef'),
                Trade.find(criteria),
                qsParam.pageNo,qsParam.pageSize , callback);
        },function(trades){
            return {
                'trades' : trades
            };
        }, {
            'afterQuery' : function (qsParam, currentPageModels, numTotal, callback) {
                ContextHelper.appendTradeContext(req.qsCurrentUserId, currentPageModels, callback);
            }
        });
    }
};

trade.getReturnReceiver = {
    'method' : 'get',
    'permissionValidators' : ['roleUserValidator'],
    'func' : function(req, res) {
        async.waterfall([function(callback) {
            Trade.findOne({
                _id : RequestHelper.parseId(req.queryString._id)
            }, function(error, trade) {
                if (error) {
                    callback(error);
                } else if (!trade) {
                    callback(errors.TradeNotExist);
                } else {
                    callback(null, trade);
                }
            });
        }, function(trade, callback) {
            Item.findOne({
                _id : trade.itemRef
            }, function(error, item) {
                if (error) {
                    callback(error);
                } else if (!item) {
                    callback(errors.ItemNotExist);
                } else {
                    callback(null, item);
                }
            });
        }, function(item, callback) {
            People.findOne({
                _id : item.shopRef
            }, function(error, people) {
                if (error) {
                    callback(error);
                } else {
                    callback(null, people);
                }
            });
        }, function(people, callback) {
            var defaultReceiver = global.qsConfig.receiver.default;
            if (!people || people.receivers === null || people.receivers.length === 0) {
                callback(null, defaultReceiver);
                return;
            }
            
            people.receivers.forEach(function(receiver) {
                if (receiver.isDefault) {
                    callback(null, receiver);
                    return;
                }
            });
            
            callback(null, people.receivers[0]);
        }], function(error, receiver) {
            ResponseHelper.response(res, error, {
                receiver : receiver
            });
        });
    }
};

trade.forge = {
    'method' : 'post',
    'permissionValidators' : ['roleUserValidator'],
    'func' : function (req, res) {
        var params = req.body;
        var highlightable = params.highlightable !== false;
        async.waterfall([function(callback) {
            People.findOne({
                '_id' : req.qsCurrentUserId
            }, callback);
        }, function(people, callback){
            if (!params.itemRef || !params.promoterRef || !params.totalFee || !params.quantity) {
                callback(errors.NotEnoughParam);
                return;
            }
            Item.findOne({
                '_id' : params.itemRef
            }, function(err, item){
                callback(err, people, item);
            });
        }, function(people, item, callback) {
            // Save trade
            var trade = new Trade();
            trade.ownerRef = req.qsCurrentUserId;
            trade.peopleSnapshot = people;
            trade.itemSnapshot = item;
            trade.itemRef = item._id;
            trade.promoterRef = RequestHelper.parseId(params.promoterRef);
            trade.quantity = params.quantity;
            trade.totalFee = params.totalFee;
            trade.pay = {
                'forge' : {
                    date : Date.now()
                }
            };
            if (highlightable) {
                trade.highlight = Date.now();
            }

            if (item.delist) {
                callback(errors.ERR_INVALID_ITEM);
                return;
            }
            if (item.skuProperties && item.skuProperties.length > 0) {
                trade.selectedSkuProperties = item.skuProperties.map(function(skuProp){
                    var strs = skuProp.split(':');
                    return strs[0] + ':' + (strs[1] || '');
                });
            }else {
                trade.selectedSkuProperties = '';
            }
            trade.save(function(err) {
                callback(err, trade, item);
            });
        }, function(trade, item, callback){
            TradeHelper.updateStatus(trade, TradeCode.STATUS_PAID, req.qsCurrentUserId, function(){});
            BonusHelper.createTradeBonus(trade, function(err){
                callback(err, trade);
            }); 
        }], function(err, trade){
            ResponseHelper.response(res, err, {
                'trade' : trade
            });
        });
    }
};

trade.own = {
    'method' : 'get',
    'permissionValidators' : ['roleUserValidator'],
    'func' : function(req, res) {
        ServiceHelper.queryPaging(req, res, function(qsParam, callback) {
            var criteria = {
                'ownerRef' : req.qsCurrentUserId,
                'status' : {'$ne' : TradeCode.STATUS_WAITING_PAY}
            };
            MongoHelper.queryPaging(Trade.find(criteria).sort({
                'create' : -1
            }), Trade.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
        }, function(trades) {
            return {
                'trades' : trades 
            };
        }, {
            'afterQuery' : function (qsParam, currentPageModels, numTotal, callback) {
                ContextHelper.appendTradeContext(req.qsCurrentUserId, currentPageModels, callback);
            }
        });
    }
};
