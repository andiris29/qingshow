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

var _create, _query, _statusTo, _getSnapShot;

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
        ['pay', 'price', 'taobaoInfo', 'logistic', 'returnLogistic'].forEach(function(element) {
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
            if (!err) {
                callback(err);
                return;
            }
            callback(null, err);
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
        var subject = "[等待买家付款]" + trade.orders[0].itemSnapshot.name + "*" + trade.orders[0].quantity + "=" + trade.orders[0].price;
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

module.exports = {
    'create' : {
        method : 'post',
        func : _create,
        permissionValidators : ['loginValidator']
    }
};
