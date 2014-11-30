var mongoose = require('mongoose');
var async = require('async');
//model
var People = require('../../model/peoples');
var RPeopleFollowPeople = require('../../model/rPeopleFollowPeople');
//util
var ContextHelper = require('../helpers/ContextHelper');
var RelationshipHelper = require('../helpers/RelationshipHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var ServerError = require('../server-error');
var ServicesUtil = require('../servicesUtil');

var _queryModels = function(req, res) {
    var param = req.queryString;
    var pageNo = param.pageNo || 1, pageSize = param.pageSize || 10;

    var buildQuery = function() {
        return People.find({
            'roles' : 1
        });
    };
    var modelDataGenFunc = function(data) {
        return {
            'peoples' : data
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

    RelationshipHelper.queryPeoples(RPeopleFollowPeople, {
        'affectedRef' : mongoose.mongo.BSONPure.ObjectID(param._id)
    }, pageNo, pageSize, 'initiatorRef', req.qsCurrentUserId, ResponseHelper.generateGeneralCallback(res));
};

var _queryFollowed = function(req, res) {
    var param = req.queryString;
    if (!param._id) {
        ServicesUtil.responseError(res, new ServerError(ServerError.RequestValidationFail));
        return;
    }
    var pageNo = param.pageNo || 1, pageSize = param.pageSize || 10;

    RelationshipHelper.queryPeoples(RPeopleFollowPeople, {
        'initiatorRef' : mongoose.mongo.BSONPure.ObjectID(param._id)
    }, pageNo, pageSize, 'affectedRef', req.qsCurrentUserId, ResponseHelper.generateGeneralCallback(res));
};

var _follow = function(req, res) {
    try {
        var param = req.body;
        var affectedRef = mongoose.mongo.BSONPure.ObjectID(param._id);
        var initiatorRef = mongoose.mongo.BSONPure.ObjectID(req.qsCurrentUserId);
    } catch (e) {
        ServicesUtil.responseError(res, new ServerError(ServerError.PeopleNotExist));
        return;
    }

    RelationshipHelper.create(RPeopleFollowPeople, initiatorRef, affectedRef, ResponseHelper.generateGeneralCallback(res));
};

var _unfollow = function(req, res) {
    try {
        var param = req.body;
        var affectedRef = mongoose.mongo.BSONPure.ObjectID(param._id);
        var initiatorRef = mongoose.mongo.BSONPure.ObjectID(req.qsCurrentUserId);
    } catch (e) {
        ServicesUtil.responseError(res, new ServerError(ServerError.PeopleNotExist));
        return;
    }

    RelationshipHelper.remove(RPeopleFollowPeople, initiatorRef, affectedRef, ResponseHelper.generateGeneralCallback(res));
};

module.exports = {
    'queryModels' : {
        method : 'get',
        func : _queryModels
    },
    'queryFollowers' : {
        method : 'get',
        func : _queryFollowers
    },
    'queryFollowed' : {
        method : 'get',
        func : _queryFollowed
    },
    'follow' : {
        method : 'post',
        func : _follow,
        needLogin : true
    },
    'unfollow' : {
        method : 'post',
        func : _unfollow,
        needLogin : true
    }
};
