var mongoose = require('mongoose');
var async = require('async');

var Trade = require('../../model/trades');
var People = require('../../model/peoples');
var Item = require('../../model/items');
var PeopleCreateTrade = require('../../model/rPeopleCreateTrade');

var RequestHelper = require('../helpers/RequestHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var TradeHelper = require('../helpers/TradeHelper');
var RelationshipHelper = require('../helpers/RelationshipHelper');

var ServerError = require('../server-error');

var qsmail = require('../../runtime/qsmail');

var _create, _query, _statusTo, _getSnapShot, _getStatusName;

_create = function(req, res) {
    var param = req.body;
    async.waterfall([function(callback) {
        var orders = param.orders;
        var trade = new Trade();
        var peopleSp = _getSnapShot(req.qsCurrentUserId, People);
        if (!peopleSp) {
            callback(ServerError.PeopleNotExist);
            return;
        }
        trade.orders = [];
        orders.forEach(function(element) {
            var itemSp = _getSnapShot(RequestHelper.parseId(element._id), Item);
            if (!itemSp) {
                callback(ServerError.ItemNotExist);
                return;
            }
            var order = {
                itemSnapshot : itemSp,
                peopleSnapshot : peopleSp,
                quantity : element.quantity,
                price : element.price,
                r : {
                    itemSnapshot : element.r.itemSnapshot,
                    peopleSnapshot : element.r.peopleSnapshot
                }
            };
            trade.orders.push(order);
        });
        ['pay', 'totalFee', 'logistic', 'returnLogistic'].forEach(function(element) {
            if (param[element]) {
                trade.set(element, param[element]);
            }
        });
        trade.save(function(err) {
            if (err) {
                callback(err);
                return;
            }
            callback(null, trade);
        });
    }, function(trade, callback) {
        // update status
        TradeHelper.updateStatus(trade, 0, req.qsCurrentUserId, function(err, trade) {
            callback(err, trade);
        });
    }, function(trade, callback) {
        // make relation
        var initiatorRef = req.qsCurrentUserId;
        var targetRef = trade._id;
        RelationshipHelper.creat(PeopleCreateTrade, initiatorRef, targetRef, function(err, relation) {
            callback(err, trade, relation);
        });
    }, function(trade, relation, callback) {
        // send mail
        var subject = "[" + _getStatusName(trade.status) + "]" + trade.orders[0].itemSnapshot.name + "*" + trade.orders[0].quantity + "=" + trade.orders[0].price;
        var content = "用户：\n"  
            + JSON.stringify(trade.orders[0].peopleSnapshot, null, 4)
            + "交易：\n"
            + JSON.stringify(trade, null, 4);

        qsmail.send(subject, content, function(err, info) {
            callback(err, trade, relation);
        });
    }], function(error, trade, relation) {
        ResponseHelper.response(res, error, {
            'trade' : trade,
            'rPeopleCreateTrade' : relation
        });
    });
};

_statusTo = function(req, res) {
    var param;
    param = req.body;
    async.waterfall([function(callback) {
        // get trade;
        Trade.findOne({
            '_id' : RequestHelper.parseId(param.id)
        }).exec(function(error, trade) {
            if (!error && !trade) {
                callback(ServerError.TradeNotExist);
            } if (error) {
                callback(error);
            } else {
                callback(null, trade);
            }
        });
    }, function(trade, callback) {
        // check status
        var newStatus = param.status;
        if (newStatus == 6) {
            //当 request.status ＝ 6: 申请退货中，检查当前状态为 1,2,3,4，不然报错
            if (trade.status >= 1 && trade.status <= 4) {
                callback(null, trade);
            } else {
                callback(ServerError.TradeStatusChangeError);
            }
        } else if (newStatus == 7) {
            //当 request.status ＝ 7: 退货中，即退货已发出，检查当前状态为 6，不然报错
            if (trade.status == 6) {
                callback(null, trade);
            } else {
                callback(ServerError.TradeStatusChangeError);
            }
        } else if (newStatus == 1) {
            //当 request.status ＝ 1: 等待倾秀代购，即买家已付款，检查当前状态为 0，不然报错
            if (trade.status == 0) {
                callback(null, trade);
            } else {
                callback(ServerError.TradeStatusChangeError);
            }
        } else if (newStatus == 2) {
            //当 request.status ＝ 2: 等待卖家发货，即倾秀已代购，检查当前状态为 1，不然报错
            if (trade.status == 1) {
                callback(null, trade);
            } else {
                callback(ServerError.TradeStatusChangeError);
            }
        } else if (newStatus == 3) {
            //当 request.status ＝ 3: 卖家已发货，检查当前状态为 2，不然报错
            if (trade.status == 2) {
                callback(null, trade);
            } else {
                callback(ServerError.TradeStatusChangeError);
            }
        } else if (newStatus == 5) {
            //当 request.status ＝ 5: 交易成功，交易自动关闭，检查当前状态为 3,4，不然报错
            if (trade.status == 3 || trade.status == 4) {
                callback(null, trade);
            } else {
                callback(ServerError.TradeStatusChangeError);
            }
        } else if (newStatus == 8) {
            //当 request.status ＝ 8: 退款成功，交易自动关闭，检查当前状态为 7，不然报错
            if (trade.status == 7) {
                callback(null, trade);
            } else {
                callback(ServerError.TradeStatusChangeError);
            }
        } else {
            callback(null, trade);
        }
    }, function(trade, callback) {
        // update trade
        if (newStatus == 1) {
            // TODO 
        } else if (newStatus == 2) {
            // TODO taobaoInfo is not exist
        } else if (newStatus == 3) {
            trade.logistic.set('company', param.logistic['company']);
            trade.logistic.set('trackingID', param.logistic['trackingID']);
        } else if (newStatus == 7) {
            trade.returnLogistic.set('company', param.returnLogistic['company']);
            trade.returnLogistic.set('trackingID', param.returnLogistic['trackingID']);
        }
        trade.status = newStatus;
        trade.save().exec(function(error, trade) {
            callback(error,trade);
        });
    }, function(trade, callback) {
        // update status
        TradeHelper.updateStatus(trade, 0, req.qsCurrentUserId, function(err, trade) {
            callback(err, trade);
        });
    }, function(trade, callback) {
        People.findOne({
            '_id' : req.qsCurrentUserId
        }).exec(function(error, people) {
            callback(error, trade, people);
        });
    }, function(trade, people callback) {
        // send mail
        var subject = "[" + _getStatusName(trade.status) + "]" + trade.orders[0].itemSnapshot.name + "*" + trade.orders[0].quantity + "=" + trade.orders[0].price;
        var content = "用户：\n"  
            + JSON.stringify(trade.orders[0].peopleSnapshot, null, 4)
            + "交易：\n"
            + JSON.stringify(trade, null, 4);

        qsmail.send(subject, content, function(err, info) {
            callback(err, trade);
        });
    }], function(error, trade) {
        ResponseHelper.response(res, error, {
            'trade' : trade
        });
    });
};

_query = function(req, res) {
    ServiceHelper.queryRelatedTrades(req, res, RPeopleCreateTrade, {
        'query' : 'initiatorRef',
        'result' : 'targetRef'
    });
}

_getSnapShot = function(id, schema) {
    async.waterfall([function(callback) {
        schema.findOne({
            '_id' : id
        }).exec(function(err, bean) {
            callback(err, bean);
        });
    }, function(bean, callback) {
        callback(null);
        return bean;
    }], function(error, bean) {
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
            return "退款成功";
    }
}

module.exports = {
    'create' : {
        method : 'post',
        func : _create,
        permissionValidators : ['loginValidator']
    },
    'statusTo' : {
        method : 'post',
        func : _statusTo,
        permissionValidators : ['loginValidator']
    },
    'query' : {
        method : 'post',
        func : _query,
        permissionValidators : ['loginValidator']
    }

};
