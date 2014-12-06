var mongoose = require('mongoose');
var async = require('async');
//model
var People = require('../../model/peoples');
var Brand = require('../../model/brands');
var RPeopleFollowBrand = require('../../model/rPeopleFollowBrand');
//util
var ContextHelper = require('../helpers/ContextHelper');
var MongoHelper = require('../helpers/MongoHelper');
var RelationshipHelper = require('../helpers/RelationshipHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var RequestHelper = require('../helpers/RequestHelper');

var ServerError = require('../server-error');

var _queryBrands = function(req, res) {
    var pageNo, pageSize, numTotal;
    async.waterfall([
    function(callback) {
        // Parse request
        try {
            var param = req.queryString;
            pageNo = parseInt(param.pageNo || 1), pageSize = parseInt(param.pageSize) || Number.POSITIVE_INFINITY;
            callback(null);
        } catch(err) {
            callback(ServerError.fromError(err));
        }
    },
    function(callback) {
        // Query
        MongoHelper.queryPaging(Brand.find().sort({
            'create' : -1
        }), Brand.find(), pageNo, pageSize, function(err, count, brands) {
            numTotal = count;
            callback(err, brands);
        });
    },
    function(brands, callback) {
        // Append context
        ContextHelper.followedByCurrentUser(RPeopleFollowBrand, req.qsCurrentUserId, brands, callback);
    }], function(err, brands) {
        // Response
        ResponseHelper.responseAsPaging(res, err, {
            'brands' : brands
        }, pageSize, numTotal);
    });
};

var _queryFollowers = function(req, res) {
    var param = req.queryString;
    if (!param._id) {
        ResponseHelper.response(res, ServerError.RequestValidationFail);
        return;
    }
    var pageNo = param.pageNo || 1, pageSize = param.pageSize || 10;

    RelationshipHelper.queryPeoples(RPeopleFollowBrand, {
        'targetRef' : mongoose.mongo.BSONPure.ObjectID(param._id)
    }, pageNo, pageSize, 'initiatorRef', req.qsCurrentUserId, function(err) {
        ResponseHelper.response(res, err);
    });
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
