var async = require('async'),
    request = require('request');

var Show = require('../../dbmodels').Show,
    Trade = require('../../dbmodels').Trade,
    People = require('../../dbmodels').People,
    PeopleCode = require('../../dbmodels').PeopleCode,
    Bonus = require('../../dbmodels').Bonus,
    BonusCode = require('../../dbmodels').BonusCode,
    SharedObject = require('../../dbmodels').SharedObject,
    SharedObjectCode = require('../../dbmodels').SharedObjectCode;

var ShareHelper = require('../../helpers/ShareHelper'),
    ResponseHelper = require('../../helpers/ResponseHelper'),
    RequestHelper = require('../../helpers/RequestHelper'),
    ServiceHelper = require('../../helpers/ServiceHelper'),
    MongoHelper = require('../../helpers/MongoHelper'),
    BonusHelper = require('../../helpers/BonusHelper');

var errors = require('../../errors');

var share = module.exports;

share.createShow = {
	method : 'post',
    permissionValidators : ['loginValidator'],
    func : function(req, res){
    	var params = req.body;
    	async.waterfall([function(callback){
    		Show.findOne({
    			'_id' : RequestHelper.parseId(params._id)
    		}, callback);
		}, function(show, callback){
			ShareHelper.create(req.qsCurrentUserId, SharedObjectCode.TYPE_SHARE_SHOW, {
				'show' : {
                    'showSnapshot': show
				}
			}, callback);
		}], function(err, sharedObject){
			ResponseHelper.response(res, err, {
                'sharedObject' : sharedObject 
            });
		});
    }
};


share.createTrade = {
	method : 'post',
	permissionValidators : ['loginValidator'],
	func : function(req, res){
		var params = req.body;
    	async.waterfall([function(callback){
    		Trade.findOne({
    			'_id' : RequestHelper.parseId(params._id)
    		}, callback);
		}, function(trade, callback){
			ShareHelper.create(req.qsCurrentUserId, SharedObjectCode.TYPE_SHARE_TRADE, {
				'trade' : {
                    'remix': JSON.parse(req.session.sharedObject)
				}
			}, callback);
		}], function(err, sharedObject){
			ResponseHelper.response(res, err, {
                'sharedObject' : sharedObject 
            });
		});
	}
};

share.createBonus = {
	'method' : 'post',
	'func' : [
        require('../middleware/injectCurrentUser'),
        require('../middleware/validateLoginAsUser'),
        function(req, res, next) {
            BonusHelper.aggregate(req.qsCurrentUserId, function(err, amountByStatus) {
                if (err) {
                    next(errors.genUnkownError(err));
                } else {
                    // Create SharedObject
                    var withdrawTotal = amountByStatus[BonusCode.STATUS_INIT] +
                        amountByStatus[BonusCode.STATUS_REQUESTED];
                    var total = withdrawTotal + 
                        amountByStatus[BonusCode.STATUS_COMPLETE];

                    ShareHelper.create(req.qsCurrentUserId, SharedObjectCode.TYPE_SHARE_BONUS, {
                        'bonus' : {
                            'ownerRef' : req.qsCurrentUserId,
                            'total' : total,
                            'withdrawTotal' : withdrawTotal
                        }
                    }, function(err, sharedObject) {
                        if (err) {
                            next(errors.genUnkownError(err));
                        } else {
                            ResponseHelper.writeData(res, {'sharedObject' : sharedObject});
                            next();
                        }
                    });
                }
            });
        },
        function(req, res, next) {
            Bonus.update(
                {'ownerRef' : req.qsCurrentUserId, 'status' : BonusCode.STATUS_INIT},
                {'status' : BonusCode.STATUS_REQUESTED},
                {'multi' : true},
                next
            );
        }
	]
};

share.query = {
    'method' : 'get',
    'func' : function(req, res) {
        ServiceHelper.queryPaging(req, res,function(qsParam, callback){
            var criteria = {};
            if (qsParam._ids && qsParam._ids.length > 0) {
                criteria._id = {
                    '$in' : RequestHelper.parseIds(qsParam._ids)
                };
                MongoHelper.queryPaging(SharedObject.find(criteria), SharedObject.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
            } else {
                callback(errors.NotEnoughParam);
            }
        },function(sharedObjects){
            return {'sharedObjects': sharedObjects};
        });
    }
};

share.withdrawBonus = {
    'method' : 'post',
    'func' : [
        require('../middleware/injectModelGenerator').generateInjectOneByObjectId(SharedObject, '_id', 'sharedObject'),
        function(req, res, next) {
            var sharedObject = req.injection.sharedObject;
            if (!sharedObject) {
                next(errors.INVALID_OBJECT_ID);
            } else {
                if (sharedObject.type !== SharedObjectCode.TYPE_SHARE_BONUS) {
                    next(errors.INVALID_SHARED_OBJECT);
                } else if (Date.now() - sharedObject.create.getTime() < 15 * 60 * 1000) {
                    next(errors.INVALID_SHARED_OBJECT);
                } else {
                    next();
                }
            }
        },
        function(req, res, next) {
            var sharedObject = req.injection.sharedObject;
            SharedObject.populate(sharedObject, {
                'path' : 'initiatorRef',
                'model' : 'peoples'
            }, next);
        },
        function(req, res, next) {
            var sharedObject = req.injection.sharedObject,
                ownerRef = sharedObject.initiatorRef;
            
            if (!ownerRef.userInfo.weixin) {
                next(errors.ERR_WEIXIN_NOT_BOUND);
            } else {
                BonusHelper.aggregate(ownerRef._id, function(err, amountByStatus) {
                    if (err) {
                        next(errors.genUnkownError(err));
                    } else {
                        var amount = amountByStatus[BonusCode.STATUS_REQUESTED];
                        // Send red pack
                        request({
                            'url' : global.qsConfig.payment.url + '/payment/wechat/sendRedPack',
                            'method' : 'post',
                            'form' : {
                                'id' : sharedObject._id.toString(),
                                'openid' : ownerRef.userInfo.weixin.openid,
                                'amount' : amount,
                                'clientIp' : RequestHelper.getIp(req),
                                'event' : global.qsConfig.bonus.event,
                                'message' : global.qsConfig.bonus.message,
                                'note' : global.qsConfig.bonus.note
                            }
                        }, function(err, response, body) {
                            try {
                                body = JSON.parse(body);
                            } catch (err) {
                                next(errors.genUnkownError(err));
                                return;
                            }
                            
                            if (body.metadata && body.metadata.error) {
                                next(errors.ERR_SEND_WEIXIN_RED_PACK_FAILED);
                            } else {
                                Bonus.update(
                                    {'ownerRef' : ownerRef._id, 'status' : BonusCode.STATUS_REQUESTED},
                                    {'$set' : {
                                        'status' : BonusCode.STATUS_COMPLETE,
                                        'weixinRedPack' : {
                                            'create' : Date.now(),
                                            'send_listid' : body.data.send_listid
                                            }
                                        }
                                    },
                                    {'multi' : true},
                                    next
                                );
                            }
                        });
                    }
                });
            }
        }
    ]
};
