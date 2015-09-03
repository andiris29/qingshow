var mongoose = require('mongoose');
var async = require('async');
var _ = require('underscore');

var Items = require('../../model/items');
var jPushAudiences = require('../../model/jPushAudiences');

var RequestHelper = require('../helpers/RequestHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var PushNotificationHelper = require('../helpers/PushNotificationHelper');

var ServerError = require('../server-error');

var item = module.exports;

item.updateExpectablePrice = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        var param = req.body;
        async.waterfall([function(callback) {
            Items.findOne({
                _id : RequestHelper.parseId(param._id)
            }, function(error, item) {
                if (error) {
                    callback(error);
                } else if (!item) {
                    callback(ServerError.ItemNotExist);
                } else {
                    callback(null, item);
                }
            });
        }, function(item, callback) {
            item.expectablePrice = RequestHelper.parseNumber(param.expectablePrice);
            item.save(function(error, item) {
                callback(error, item);
            });
        }, function(item, callback) {
            _itemPriceChanged(item, function() {
                // do nothing
            });
            callback(null, item);
        }], function(error, item) {
            ResponseHelper.response(res, error, {
                item : item
            });
        });
    }
};

item.removeExpectablePrice = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        var param = req.body;
        async.waterfall([function(callback) {
            Items.collection({
                _id : RequestHelper.parseId(param._id)
            }, {
                $unset : { expectablePrice : 0 }
            }, {
            }, function(error) {
                callback(error);
            });
        }, function(callback) {
            Items.findOne({
                _id : RequestHelper.parseId(param._id)
            }, function(error, item) {
                callback(error, item);
            });
        }], function(error, item) {
            ResponseHelper.response(res, error, {
                item : item
            });
        });
    }
};

var _itemPriceChanged = function(item, callback) {
    async.waterfall([
    function(callback) {
        Trade.find({
            'itemRef' : item._id,
            'status' : 0
        }).exec(callback);
    },
    function(trades, callback) {
        var tasks = trades.map(function(trade) {
            return function(cb) {
                async.waterfall([
                function(cb2) {
                    jPushAudiences.find({
                        peopleRef : trade.ownerRef
                    }).exec(function(err, infos) {
                        cb2(err, infos);
                    });
                },
                function(infos, cb2) {
                    var registrationIDs = [];
                    infos.forEach(function(target) {
                        registrationIDs.push(target.registrationId);
                    });
                    PushNotificationHelper.push(registrationIDs, PushNotificationHelper.MessageItemPriceChanged, {
                        'command' : PushNotificationHelper.CommandItemPriceChanged,
                        '_id' : req.body._id,
                        '_tradeId' : trade._id.toString(),
                        'actualPrice' : RequestHelper.parseNumber(req.body.actualPrice)
                    }, cb2);
                }], cb);
            };
        });
        async.parallel(tasks, function(err) {
            callback();
        });
    }], function(err) {
        callback(err);
    });
};

