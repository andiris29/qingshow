var mongoose = require('mongoose');
var async = require('async');
//model
var People = require('../../model/peoples');
var Brand = require('../../model/brands');
var RPeopleFollowBrand = require('../../model/rPeopleFollowBrand');
//util
var ContextHelper = require('../helpers/ContextHelper');
var RelationshipHelper = require('../helpers/RelationshipHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var ServerError = require('../server-error');
var ServicesUtil = require('../servicesUtil');

var _queryBrands = function(req, res) {
    var param = req.queryString;
    var pageNo = param.pageNo || 1, pageSize = param.pageSize || Number.POSITIVE_INFINITY;

    var buildQuery = function() {
        return Brand.find({
            'type' : 0
        });
    };
    var modelDataGenFunc = function(data) {
        return {
            'brands' : data
        };
    };
    ServicesUtil.sendSingleQueryToResponse(res, buildQuery, null, modelDataGenFunc, pageNo, pageSize, function(peoples, callback) {
        ContextHelper.followedByCurrentUser(req.qsCurrentUserId, peoples, function(err, peoples) {
            if (err) {
                ServicesUtil.responseError(res, new ServerError(err));
            } else {
                callback(peoples);
            }
        });
    });
};

var _queryFollowers = function(req, res) {
    var param = req.queryString;
    if (!param._id) {
        ServicesUtil.responseError(res, new ServerError(ServerError.RequestValidationFail));
        return;
    }
    var pageNo = param.pageNo || 1, pageSize = param.pageSize || 10;

    RelationshipHelper.queryPeoples(RPeopleFollowBrand, {
        'targetRef' : mongoose.mongo.BSONPure.ObjectID(param._id)
    }, pageNo, pageSize, 'initiatorRef', req.qsCurrentUserId, ResponseHelper.generateGeneralCallback(res));
};

var _follow = function(req, res) {
    try {
        var param = req.body;
        var targetRef = mongoose.mongo.BSONPure.ObjectID(param._id);
        var initiatorRef = mongoose.mongo.BSONPure.ObjectID(req.qsCurrentUserId);
    } catch (e) {
        ServicesUtil.responseError(res, new ServerError(ServerError.PeopleNotExist));
        return;
    }

    RelationshipHelper.create(RPeopleFollowBrand, initiatorRef, targetRef, ResponseHelper.generateGeneralCallback(res));
};

var _unfollow = function(req, res) {
    try {
        var param = req.body;
        var targetRef = mongoose.mongo.BSONPure.ObjectID(param._id);
        var initiatorRef = mongoose.mongo.BSONPure.ObjectID(req.qsCurrentUserId);
    } catch (e) {
        ServicesUtil.responseError(res, new ServerError(ServerError.PeopleNotExist));
        return;
    }

    RelationshipHelper.remove(RPeopleFollowBrand, initiatorRef, targetRef, ResponseHelper.generateGeneralCallback(res));
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
