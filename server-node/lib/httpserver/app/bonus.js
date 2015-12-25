var async = require('async');

var Bonus = require('../../dbmodels').Bonus,
    BonusCode = require('../../dbmodels').BonusCode;

var RequestHelper = require('../../helpers/RequestHelper'),
    ResponseHelper = require('../../helpers/ResponseHelper'),
    MongoHelper = require('../../helpers/MongoHelper'),
    ServiceHelper = require('../../helpers/ServiceHelper'),
    BonusHelper = require('../../helpers/BonusHelper');

var errors = require('../../errors');

var bonus = module.exports;

bonus.query = {
    'method' : 'get',
    'permissionValidators' : ['roleUserValidator'],
    'func' : function(req, res) {
        ServiceHelper.queryPaging(req, res, function(qsParam, callback) {
            if (!qsParam._ids || !qsParam._ids.length) {
                callback(errors.NotEnoughParam);
                return;
            }
            var criteria = {};
            criteria._id = {
                '$in' : RequestHelper.parseIds(qsParam._ids)
            };
            MongoHelper.queryPaging(
                Bonus.find(criteria).populate('trigger.tradeRef').populate('participants'), 
                Bonus.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
        }, function(bonuses) {
            return {
                'bonuses' : bonuses
            };
        });
    }
};

bonus.own = {
    'method' : 'get',
    'permissionValidators' : ['roleUserValidator'],
    'func' : function(req, res) {
        ServiceHelper.queryPaging(req, res, function(qsParam, callback) {
            var criteria = {
                'ownerRef' : req.qsCurrentUserId
            };
            MongoHelper.queryPaging(Bonus.find(criteria).sort({'create' : -1}).populate('trigger.tradeRef'), 
                Bonus.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
        }, function(bonuses) {
            return {
                'bonuses' : bonuses 
            };
        });
    }
};

bonus.withdraw = {
    'method' : 'post',
    'func' : [
        require('../middleware/injectCurrentUser'),
        require('../middleware/validateLoginAsUser'),
        function(req, res, next) {
            var ownerRef = req.injection.qsCurrentUser;
            
            if (!ownerRef.userInfo.weixin) {
                next(errors.ERR_WEIXIN_NOT_BOUND);
            } else {
                BonusHelper.aggregate(ownerRef._id, function(err, amountByStatus) {
                    if (err) {
                        next(errors.genUnkownError(err));
                    } else {
                        var amount = amountByStatus[BonusCode.STATUS_INIT] +
                            amountByStatus[BonusCode.STATUS_REQUESTED];
                        // Send red pack
                        request({
                            'url' : global.qsConfig.payment.url + '/payment/wechat/sendRedPack',
                            'method' : 'post',
                            'form' : {
                                'id' : Date.now().toString(),
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
                                    {
                                        'ownerRef' : ownerRef._id, 
                                        '$or' : [{'status' : BonusCode.STATUS_INIT}, {'status' : BonusCode.STATUS_REQUESTED}]},
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
