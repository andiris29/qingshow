var mongoose = require('mongoose'),
    async = require('async'),
    _ = require('underscore');

var Item = require('../../dbmodels').Item,
    People = require('../../dbmodels').People,
    PeopleCode = require('../../dbmodels').PeopleCode;

var RequestHelper = require('../../helpers/RequestHelper'),
    ResponseHelper = require('../../helpers/ResponseHelper'),
    NotificationHelper = require('../../helpers/NotificationHelper'),
    MongoHelper = require('../../helpers/MongoHelper');

var loggers = require('../../runtime/loggers');

var errors = require('../../errors');

var goblin = require('../../goblin');

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


item.findOneAndStartSync = {
    'method' : 'get',
    'func' : [
        function(req, res, next) {
            if (req.queryString.version === global.qsConfig.goblin.supportedVersion) {
                req.injection.criteria = {
                    '$or' : RequestHelper.parseArray(req.queryString.domains).map(function(domain) {
                        return {'sourceInfo.domain' : domain};
                    }),
                    'syncEnabled' : {'$ne' : false},
                    'syncStartAt' : null
                };
                next();
            } else {
                next(errors.GoblinSlaveDisabled);
            }
        },
        function(req, res, next) {
            // Item never synced
            Item.findOne(_.extend({'sync' : null}, req.injection.criteria)).sort({'create' : 1}).exec(function(err, item) {
                req.injection.item = item;
                next();
            });
        },
        function(req, res, next) {
            if (req.injection.item) {
                next();
            } else {
                // Item oldest sync requested
                Item.findOne(_.extend({'syncRequestAt' : {'$ne' : null}}, req.injection.criteria)).sort({'syncRequestAt' : 1}).exec(function(err, item) {
                    req.injection.item = item;
                    next();
                });
            }
        },
        function(req, res, next) {
            if (req.injection.item) {
                next();
            } else {
                // Item oldest synced
                Item.findOne(req.injection.criteria).sort({'sync' : 1}).exec(function(err, item) {
                    req.injection.item = item;
                    next();
                });
            }
        },
        function(req, res, next) {
            req.injection.item.syncStartAt = new Date();
            req.injection.item.save(function() {
                ResponseHelper.writeData(res, {'item' : req.injection.item});
                next();
            });
        }
    ]
};

item.syncComplete = {
    'method' : 'post',
    'func' : [
        require('../middleware/injectModelGenerator').generateInjectOneByObjectId(Item, 'itemRef'),
        function(req, res, next) {
            // Sync sourceInfo
            var item = req.injection.itemRef;
            if (!item.sourceInfo) {
                item.sourceInfo = goblin.parseUrl(item.source);
            }
            next();
        },
        function(req, res, next) {
            // Sync itemInfo
            var item = req.injection.itemRef,
                itemInfo = req.body.itemInfo;
            
            if (itemInfo) {
                item.delist = null;
                item.price = itemInfo.price;
                item.promoPrice = itemInfo.promo_price;
                item.skuProperties = itemInfo.skuProperties;
                var skuTable = {};
                itemInfo.skuTable = itemInfo.skuTable || {};
                for (var key in itemInfo.skuTable) {
                    if (itemInfo.skuTable.hasOwnProperty(key)) {
                        var value = itemInfo.skuTable[key];
                        // skuTable key should not contain '.' to avoid mongo error
                        key = key.replace(/\./g, '');
                        skuTable[key] = value;
                    }
                }
                item.skuTable = skuTable;                
            } else {
                item.delist = item.delist || new Date();
            }
            next();
        },
        function(req, res, next) {
            // Sync shopInfo
            var item = req.injection.itemRef,
                itemInfo = req.body.itemInfo;
            if (!itemInfo || item.shopRef) {
                next();
            } else {
                var shopInfo = itemInfo.shopInfo;
                if (!shopInfo) {
                    if (item.sourceInfo.domain === goblin.domain.HM) {
                        shopInfo = {'shopId' : 'hm', 'shopName' : 'H&M'};
                    } else if (item.sourceInfo.domain === goblin.domain.JAMY) {
                        shopInfo = {'shopId' : 'jamy', 'shopName' : 'JAMY'};
                    }
                }
                
                if (shopInfo) {
                    People.findOne({
                        'userInfo.id' : shopInfo.shopId,
                        'role' : PeopleCode.ROLE_SHOP
                    }, function(err, people) {
                        if (people) {
                            item.shopRef = people._id;
                            next();
                        } else {
                            people = new People({
                                'role' : PeopleCode.ROLE_SHOP,
                                'nickname': shopInfo.shopName,
                                'userInfo' : {
                                    'id' : shopInfo.shopId,
                                    'password' : shopInfo.shopId
                                }
                            });
                            people.save(function(err) {
                                item.shopRef = people._id;
                                next();
                            });
                        }
                    });
                } else {
                    next();
                }
            }
        },
        function(req, res, next) {
            var item = req.injection.itemRef;
            item.syncStartAt = null;
            item.sync = new Date();
            item.save(function(err) {
                ResponseHelper.writeData(res, {'item' : item});
                next();
            });
        }
    ]
};

item.findOneBySourceInfo = {
    'method' : 'get',
    'func' : [
        function(req, res, next) {
            Item.findOne({
                'sourceInfo.domain': req.queryString.domain,
                'sourceInfo.id': req.queryString.id
            }, function(err, item) {
                if (err) {
                    next(err);
                } else {
                    if (!item) {
                        next(errors.ItemNotExist);
                    } else {
                        ResponseHelper.writeData(res, {'item' : item});
                        next();
                    }
                }
            });
        }
    ]
};
