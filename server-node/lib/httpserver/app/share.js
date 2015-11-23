var async = require('async'),
    request = require('request');

var Show = require('../../dbmodels').Show,
    Trade = require('../../dbmodels').Trade,
    People = require('../../dbmodels').People,
    PeopleCode = require('../../dbmodels').PeopleCode,
    SharedObject = require('../../dbmodels').SharedObject,
    SharedObjectCode = require('../../dbmodels').SharedObjectCode;

var ShareHelper = require('../../helpers/ShareHelper'),
    ResponseHelper = require('../../helpers/ResponseHelper'),
    RequestHelper = require('../../helpers/RequestHelper'),
    ServiceHelper = require('../../helpers/ServiceHelper'),
    MongoHelper = require('../../helpers/MongoHelper');

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
			ShareHelper.create(req.qsCurrentUserId, 0, ShareHelper.shareShowTitle ,{
				'show' : {
					_id : show._id,
					cover : show.cover,
					coverForeground : show.coverForeground 
				}
			}, callback);
		}], function(err, shareObject){
			ResponseHelper.response(res, err, {
                'sharedObject' : shareObject 
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
			ShareHelper.create(req.qsCurrentUserId, 1, ShareHelper.shareTradeTitle, {
				'trade' : {
					_id : trade._id,
					totalFee : trade.totalFee,
					quantity : trade.quantity,
					itemSnapshot : {
						name : trade.itemSnapshot.name,
						promoPrice : trade.itemSnapshot.promoPrice,
						thumbnail : trade.itemSnapshot.thumbnail
					}
				}
			}, callback);
		}], function(err, shareObject){
			ResponseHelper.response(res, err, {
                'sharedObject' : shareObject 
            });
		});
	}
};

share.createBonus = {
	method : 'post',
	permissionValidators : ['loginValidator'],
	func : function(req, res){
		var params = req.body;
    	async.waterfall([function(callback){
    		People.findOne({
    			'_id' : RequestHelper.parseId(params._id)
    		}, callback);
		}, function(people, callback){
			var total = 0;
			var withdrawTotal = 0;
			if (people.bonuses && people.bonuses.length > 0) {
				people.bonuses.forEach(function(bonus){
					if (bonus.status === PeopleCode.BONUS_STATUS_INIT || bonus.status === PeopleCode.BONUS_STATUS_REQUESTED) {
						withdrawTotal += bonus.money;   
					}
					total += bonus.money;

                    if (bonus.status === PeopleCode.BONUS_STATUS_INIT) {
                        bonus.status = PeopleCode.BONUS_STATUS_REQUESTED
                    }
				});
			}

            people.save(function(err){});

			ShareHelper.create(req.qsCurrentUserId, 2, ShareHelper.shareBonusTitle, {
				'bonus' : {
					ownerRef : people._id,
					total : total,
					withdrawTotal : withdrawTotal
				}
			}, callback);
		}], function(err, shareObject){
			ResponseHelper.response(res, err, {
                'sharedObject' : shareObject 
            });
		});
	}
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
            }
            MongoHelper.queryPaging(SharedObject.find(criteria), SharedObject.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
        },function(sharedObjects){
            return {'sharedObjects': sharedObjects};
        });
    }
};

share.withdrawBonus = {
    'method' : 'post',
    'func' : [
        require('../middleware/injectModelGenerator').generateInjectOneByObjectId(SharedObject, 'sharedObject'),
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
                people = sharedObject.initiatorRef;
            
            if (!people.userInfo.weixin) {
                next(errors.ERR_WEIXIN_NOT_BOUND);
            } else {
                var amount = 0;
                people.bonuses.forEach(function(bonus) {
                    if (bonus.status === PeopleCode.BONUS_STATUS_INIT) {
                        bonus.status = PeopleCode.BONUS_STATUS_REQUESTED;
                        amount = amount + bonus.money;
                    }
                });
                
                request({
                    'url' : global.qsConfig.payment.url + '/payment/wechat/sendRedPack',
                    'method' : 'post',
                    'form' : {
                        'id' : sharedObject._id.toString(),
                        'openid' : people.userInfo.weixin.openid,
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
                        people.bonuses.forEach(function(bonus) {
                            bonus.status = PeopleCode.BONUS_STATUS_COMPLETE;
                            bonus.weixinRedPackId = body.data.send_listid;
                        });
                        people.save(function(err, people){
                            next(err);    
                        });
                    }
                });
            }
        }
    ]
};
