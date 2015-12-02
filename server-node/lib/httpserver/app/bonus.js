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
            var criteria = {};
            if (qsParam._ids || qsParam._ids.length > 0) {
                criteria._id = {
                    '$in' : RequestHelper.parseIds(qsParam._ids)
                };
            }
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
            MongoHelper.queryPaging(Bonus.find(criteria).sort({'create' : -1}), 
                Bonus.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
        }, function(bonuses) {
            return {
                'bonuses' : bonuses 
            };
        });
    }
};
