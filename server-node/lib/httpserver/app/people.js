var mongoose = require('mongoose');
var async = require('async');
//model
var People = require('../../dbmodels').People;
var Trade = require('../../dbmodels').Trade;
var RPeopleFollowPeople = require('../../dbmodels').RPeopleFollowPeople;
//util
var ServiceHelper = require('../../helpers/ServiceHelper');
var MongoHelper = require('../../helpers/MongoHelper');
var ContextHelper = require('../../helpers/ContextHelper');
var RelationshipHelper = require('../../helpers/RelationshipHelper');
var ResponseHelper = require('../../helpers/ResponseHelper');
var RequestHelper = require('../../helpers/RequestHelper');
var TradeHelper = require('../../helpers/TradeHelper');

var errors = require('../../errors');

var people = module.exports;

people.queryFollowers= {
    method : 'get',
    func : function(req, res) {
        ServiceHelper.queryRelatedPeoples(req, res, RPeopleFollowPeople, {
            'query' : 'targetRef',
            'result' : 'initiatorRef'
        });
    }
};

people.queryFollowingPeoples= {
    method : 'get',
    func : function(req, res) {
        ServiceHelper.queryRelatedPeoples(req, res, RPeopleFollowPeople, {
            'query' : 'initiatorRef',
            'result' : 'targetRef'
        });
    }
};

people.follow = {
    method : 'post',
    permissionValidators : ['loginValidator'],
    func : function(req, res) {
        try {
            var param = req.body;
            var targetRef = RequestHelper.parseId(param._id);
            var initiatorRef = RequestHelper.parseId(req.qsCurrentUserId);

            RelationshipHelper.create(RPeopleFollowPeople, initiatorRef, targetRef, function(err) {
                ResponseHelper.response(res, err);
            });
        } catch (e) {
            ResponseHelper.response(res, errors.PeopleNotExist);
            return;
        }
    }
};

people.unfollow = {
    method : 'post',
    permissionValidators : ['loginValidator'],
    func: function(req, res) {
        try {
            var param = req.body;
            var targetRef = RequestHelper.parseId(param._id);
            var initiatorRef = RequestHelper.parseId(req.qsCurrentUserId);

            RelationshipHelper.remove(RPeopleFollowPeople, initiatorRef, targetRef, function(err) {
                ResponseHelper.response(res, err);
            });
        } catch (e) {
            ResponseHelper.response(res, errors.PeopleNotExist);
            return;
        }
    }
};

people.query = {
    method : 'get',
    func : function(req, res) {
        var _ids;
        async.waterfall([
        function(callback) {
            // Parser req
            try {
                _ids = RequestHelper.parseIds(req.queryString._ids);
                callback(null);
            } catch (err) {
                callback(errors.genUnkownError(err));
            }
        },
        function(callback) {
            // Query & populate
            People.find({
                '_id' : {
                    '$in' : _ids
                }
            }).exec(callback);
        },
        function(peoples, callback) {
            ContextHelper.appendPeopleContext(req.qsCurrentUserId, peoples, callback);
        }], function(err, peoples) {
            ResponseHelper.response(res, err, {
                'peoples' : peoples 
            });
        });
    }
};


people.queryBuyers = {
    method : 'post',
    func : function(req, res) {
        var itemRef = req.body.itemRef;
        Trade.find({
            'itemRef' : itemRef
        }).exec(function(err, trades){
            if (err) {
                ResponseHelper.response(res, errors.genUnkownError(), {});
                return;
            }
            var ownerRefs = trades.map(function(trade){
                return trade.ownerRef;
            });
            var criteria = {
                '_id' : {
                    $in : ownerRefs
                }
            };
            ServiceHelper.queryPaging(req, res, function(qsParam, callback){
                MongoHelper.queryPaging(People.find(criteria), People.find(criteria), qsParam.pageNo, 
                    qsParam.pageSize, function(err, models, count){
                        callback(err, models, count);
                    });
            },function(peoples){
                return {
                    'peoples' : peoples
                }
            });
        });            
    }
}


