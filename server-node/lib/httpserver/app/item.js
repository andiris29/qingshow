
var mongoose = require('mongoose');
var async = require('async');
var _ = require('underscore');

var Items = require('../../models').Item;
var Trade = require('../../models').Trade;
var People = require('../../models').People;

var jPushAudiences = require('../../models').JPushAudience;
var RequestHelper = require('../../helpers/RequestHelper');
var ResponseHelper = require('../../helpers/ResponseHelper');
var MongoHelper = require('../../helpers/MongoHelper');
var PushNotificationHelper = require('../../helpers/PushNotificationHelper');
var TradeHelper = require('../../helpers/TradeHelper');
var URLParser = require('../../scheduled/goblin/common/URLParser');
var qsftp = require('../../runtime').ftp;
var GoblinScheduler = require("../../scheduled/goblin/scheduler/GoblinScheduler");

var ItemSyncService = require("../../scheduled/goblin/common/ItemSyncService");
var ServerError = require('../server-error');
var GoblinError = require('../../scheduled/goblin/common/GoblinError');

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
            async.waterfall([function(cb){
                Trade.find({
                    'itemRef' : item._id,
                    'status' : 0
                }).exec(function(error, trades) {
                    if (error) {
                        cb(ServerError.TradeNotExist);
                    }else {
                        cb(null, trades);
                    }
                });
            },
            function(trades, cb){
                _itemPriceChanged(trades, RequestHelper.parseNumber(param.expectablePrice),function(){
                });

                trades.forEach(function(trade){
                    TradeHelper.removeExpectableTrades(trade._id, trade.ownerRef, function(err){
                        if (err) {
                            cb(err)
                        }
                    });
                    TradeHelper.pushNewExpectableTrades(trade._id, trade.ownerRef, param.expectablePrice, function(err){
                        if (err) {
                            cb(err)
                        }
                    });
                });
                cb(null, trades);
            }],function(err){
                if (err) {
                    callback(err);
                }else {
                    callback(null, item);
                }
            })
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
            Items.findOneAndUpdate({
                _id : RequestHelper.parseId(param._id)
            }, {
                $unset : { expectablePrice : 0 }
            }, {
            }, function(error) {
                callback(error);
            });
        }, function(callback){
            Trade.find({
                'itemRef' : RequestHelper.parseId(param._id)
            }, function(err, trades){
                if (err) {
                    callback(err);
                }else{
                    callback(null, trades);
                }
            })
        }, function(trades, callback){
            trades.forEach(function(trade){
                TradeHelper.removeExpectableTrades(trade._id, trade.ownerRef, function(err){
                    callback(err);
                });
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


item.sync = {
    method : 'post',
    func : function (req, res) {
        async.waterfall([
            function (callback) {
                var itemId = RequestHelper.parseId(req.body._id);
                GoblinScheduler.registerItemWithId(itemId, callback);
                //ItemSyncService.syncItemWithItemId(itemId, callback);
            }
        ], function (err, item) {
            if (err) {
                if (err.domain === GoblinError.Domain) {
                    err = new ServerError(ServerError.GoblinError, err.description, err);
                }
                ResponseHelper.response(res, err);
            } else if (!item) {
                ResponseHelper.response(res, ServerError.ItemNotExist);
            } else {
                ResponseHelper.response(res, null, {
                    'item' : item
                });
            }
        });
    }
};

item.delist = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        async.waterfall([function(callback) {
            Items.findOne({
                _id : RequestHelper.parseId(req.body._id)
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
            item.delist = Date.now();
            item.syncEnabled = false;
            item.save(function(error, item) {
                callback(error, item);
            });
        }], function(error, item) {
            ResponseHelper.response(res, error, {
                item : item
            });
        });
    }
};

item.create = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        var source = req.body.source;
        var criteria;
        if (URLParser.isFromTmall(source) || URLParser.isFromTaobao(source)) {
            var id = URLParser.getIidFromSource(source);
            criteria = {
                source : new RegExp(id)
            }
        } else {
            criteria = {
                source : source
            };
        }
        async.waterfall([function(callback) {
            Items.findOne(criteria, function(error, item) {
                if (error) {
                    callback(error);
                } else if (item) {
                    callback(item);
                } else {
                    var newItem = new Item({
                        source : source,
                        syncEnabled : true
                    });

                    newItem.save(function(error, item) {
                        callback(error, item);
                    });
                }
            });
        }], function(error, item) {
            ResponseHelper.response(res, error, {
                item : item
            });
        });
    }
};

item.list = {
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
            if (param.name && param.name.length > 0) { 
                item.name = param.name;
            }
            if (param.categoryRef && param.categoryRef.length > 0) {
                item.categoryRef = RequestHelper.parseId(param.categoryRef);
            }
            item.list = new Date();
            RequestHelper.parseFile(req, global.qsConfig.uploads.item.thumbnail.ftpPath, [
                {'suffix' : '_s', 'rate' : 0.5},
                {'suffix' : '_xs', 'rate' : 0.25}
            ], function(err, fields, file) {
                if (file) {
                    item.set('cover', global.qsConfig.uploads.item.thumbnail.exposeToUrl + '/' + path.relative(global.qsConfig.uploads.item.thumbnail.ftpPath, file.path));
                    return;
                }

                item.save(function(error, item) {
                    callback(error, item);
                });
            });
        }, function(item, callback) {
            ItemSyncService.syncItem(item, callback);
        }], function(error, item) {
            ResponseHelper.response(res, error, {
                item : item
            });
        });
    }
};

var _itemPriceChanged = function(trades, price, callback) {
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
                        'command' : PushNotificationHelper.CommandItemExpectablePriceUpdated,
                        '_id' : trade._id
                    }, cb2);
                }], cb);
        };
    });
    async.parallel(tasks, function(err) {
        if (err) {
            callback(err);
        }
    });
};

item.query = {
    'method' : 'get',
    'func' : function(req, res) {
        ServiceHelper.queryPaging(req, res,function(qsParam, callback){
            var criteria = {};
            if (qsParam._ids && qsParam._ids.length > 0) {
                criteria._id = {
                    '$in' : RequestHelper.parseIds(qsParam._ids)
                };
            }
            MongoHelper.queryPaging(Items.find(criteria), Items.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
        },function(items){
            return {'items': items}
        })
    }
};


