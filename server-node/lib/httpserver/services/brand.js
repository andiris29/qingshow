var mongoose = require('mongoose');
var async = require('async'), _ = require('underscore');
//model
var People = require('../../model/peoples');
var Brand = require('../../model/brands');
var RPeopleFollowBrand = require('../../model/rPeopleFollowBrand');
//util
var ServiceHelper = require('../helpers/ServiceHelper');
var ContextHelper = require('../helpers/ContextHelper');
var MongoHelper = require('../helpers/MongoHelper');
var RelationshipHelper = require('../helpers/RelationshipHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var RequestHelper = require('../helpers/RequestHelper');

var ServerError = require('../server-error');

var _queryBrands = function(req, res) {
    ServiceHelper.queryPaging(req, res, function(qsParam, callback) {
        // querier
        var criteria = {};
        if (qsParam.type !== undefined) {
            criteria.type = qsParam.type;
        }
        // Query
        MongoHelper.queryPaging(Brand.find(criteria).sort({
            'create' : -1
        }), Brand.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
    }, function(models) {
        // responseDataBuilder
        return {
            'brands' : models
        };
    }, {
        'postParseRequest' : function(raw) {
            return {
                'type' : raw.type
            };
        },
        'postQuery' : function(qsParam, currentPageModels, numTotal, callback) {
            ContextHelper.appendBrandContext(req.qsCurrentUserId, currentPageModels, callback);
        }
    });
};

var _queryFollowers = function(req, res) {
    ServiceHelper.queryRelatedPeoples(req, res, RPeopleFollowBrand, 'targetRef', 'initiatorRef');
};

var _follow = function(req, res) {
    try {
        var param = req.body;
        var targetRef = mongoose.mongo.BSONPure.ObjectID(param._id);
        var initiatorRef = mongoose.mongo.BSONPure.ObjectID(req.qsCurrentUserId);
    } catch (e) {
        ResponseHelper.response(res, ServerError.PeopleNotExist);
        return;
    }

    RelationshipHelper.create(RPeopleFollowBrand, initiatorRef, targetRef, function(err) {
        ResponseHelper.response(res, err);
    });
};

var _unfollow = function(req, res) {
    try {
        var param = req.body;
        var targetRef = mongoose.mongo.BSONPure.ObjectID(param._id);
        var initiatorRef = mongoose.mongo.BSONPure.ObjectID(req.qsCurrentUserId);
    } catch (e) {
        ResponseHelper.response(res, ServerError.PeopleNotExist);
        return;
    }

    RelationshipHelper.remove(RPeopleFollowBrand, initiatorRef, targetRef, function(err) {
        ResponseHelper.response(res, err);
    });
};

module.exports = {
    'queryBrands' : {
        method : 'get',
        func : _queryBrands
    },
    'queryFollowers' : {
        method : 'get',
        func : _queryFollowers
    },
    'follow' : {
        method : 'post',
        func : _follow,
        permissionValidators : ['loginValidator']
    },
    'unfollow' : {
        method : 'post',
        func : _unfollow,
        permissionValidators : ['loginValidator']
    }
};
