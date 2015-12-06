var mongoose = require('mongoose'),
    async = require('async'),
    _ = require('underscore');

var Item = require('../../dbmodels').Item;

var RequestHelper = require('../../helpers/RequestHelper'),
    ResponseHelper = require('../../helpers/ResponseHelper'),
    NotificationHelper = require('../../helpers/NotificationHelper'),
    MongoHelper = require('../../helpers/MongoHelper');

var URLParser = require('../../goblin-common/URLParser'),
    GoblinError = require('../../goblin-common/GoblinError');
var GoblinScheduler = require('./goblin/GoblinScheduler');

var loggers = require('../../runtime/loggers');

var errors = require('../../errors');

var item = module.exports;

item.updateExpectable = {
    'method' : 'post',
    'func' : [
        require('../middleware/injectModelGenerator').generateInjectOneByObjectId(Item, '_id', 'itemRef'),
        function(req, res, next) {
            req.injection.itemRef.expectable = {
                'reduction' : RequestHelper.parseNumber(req.body.reduction),
                'message' : req.body.message,
                'expired' : req.body.expired === true
            };
            
            req.injection.itemRef.save(function(err) {
                if (err) {
                    next(errors.genUnkownError(err));
                } else {
                    ResponseHelper.writeData(res, {'item' : req.injection.itemRef});
                    next();
                }
            });
        }
    ]
};

item.removeExpectable = {
    'method' : 'post',
    'func' : [
        function(req, res, next) {
            Item.findOneAndUpdate({
                '_id' : RequestHelper.parseId(req.body._id)
            }, {
                '$unset' : { 'expectable' : -1 }
            }, {
            }, function(err) {
                next(err);
            });
        },
        require('../middleware/injectModelGenerator').generateInjectOneByObjectId(Item, '_id', 'itemRef'),
        function(req, res, next) {
            ResponseHelper.writeData(res, {'item' : req.injection.itemRef});
            next();
        }
    ]
};


item.sync = {
    method : 'post',
    func : function (req, res) {
        async.waterfall([
            function (callback) {
                var itemId = RequestHelper.parseId(req.body._id);
                var invoke = false;
                
                _.delay(function() {
                    if (!invoke) {
                        invoke = true;
                        loggers.get('item-sync').info({'result' : 'miss', '_id' : req.body._id});
                        Item.findOne({_id : itemId}, callback);
                    }
                }, global.qsConfig.item.sync.timeout || 10000);
                
                GoblinScheduler.registerItemWithId(itemId, function(err, item) {
                    if (!invoke) {
                        invoke = true;
                        if (!err) {
                            loggers.get('item-sync').info({'result' : 'hit', '_id' : req.body._id});
                            callback(err, item);
                        } else {
                            loggers.get('item-sync').info({'result' : 'error', '_id' : req.body._id, 'error' : err});
                            Item.findOne({_id : itemId}, callback);
                        }
                    }
                });
            }
        ], function (err, item) {
            if (err) {
                if (err.domain === GoblinError.Domain) {
                    err = errors.genGoblin(err.description, err);
                }
                ResponseHelper.response(res, err);
            } else if (!item) {
                ResponseHelper.response(res, errors.ItemNotExist);
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
            Item.findOne({
                _id : RequestHelper.parseId(req.body._id)
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
            Item.findOne(criteria, function(error, item) {
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

item.query = {
    'method' : 'get',
    'func' : function(req, res) {
        ServiceHelper.queryPaging(req, res,function(qsParam, callback){
            if (!qsParam._ids || !qsParam._ids.length) {
                callback(errors.NotEnoughParam);
                return;
            }
            var criteria = {};
            criteria._id = {
                '$in' : RequestHelper.parseIds(qsParam._ids)
            };
            MongoHelper.queryPaging(
                Item.find(criteria).populate('shopRef'), 
                Item.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
        },function(items){
            return {'items': items};
        });
    }
};


