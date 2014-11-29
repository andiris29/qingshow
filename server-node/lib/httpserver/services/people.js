var mongoose = require('mongoose');
var async = require('async');
//model
var People = require('../../model/peoples');
var RPeopleFollowPeople = require('../../model/rPeopleFollowPeople');
//util
var ContextHelper = require('../helpers/ContextHelper');
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
        ContextHelper.followedByCurrentUser(req.currentUser, peoples, function(err, peoples) {
            if (err) {
                ServicesUtil.responseError(res, new ServerError(err));
            } else {
                callback(peoples);
            }
        });
    });
};

var _queryFollowers = function(req, res) {
    _followerAndFollowed(req, res, 'affectedRef', 'initiatorRef');
};

var _queryFollowed = function(req, res) {
    _followerAndFollowed(req, res, 'initiatorRef', 'affectedRef');
};

var _followerAndFollowed = function(req, res, masterField, slaveField) {
    var param = req.queryString;
    if (!param._id) {
        ServicesUtil.responseError(res, new ServerError(ServerError.RequestValidationFail));
        return;
    }
    var pageNo = param.pageNo || 1, pageSize = param.pageSize || 10;

    var criteria = {};
    criteria[masterField] = mongoose.mongo.BSONPure.ObjectID(param._id);

    async.waterfall([
    function(callback) {
        ServicesUtil.limitQuery(RPeopleFollowPeople.find(criteria), pageNo, pageSize).exec(callback);
    },
    function(relationships, callback) {
        var _ids = [];
        relationships.forEach(function(r) {
            _ids.push(r[slaveField]);
        });
        People.find({
            '_id' : {
                '$in' : _ids
            }
        }, callback);
    },
    function(peoples, callback) {
        ContextHelper.followedByCurrentUser(req.currentUser, peoples, callback);
    }], function(err, peoples) {
        if (err) {
            ServicesUtil.responseError(res, new ServerError(err));
        } else {
            res.json(peoples);
        }
    });
};
// Request
//  _id string ObjectId in peoples
var _follow = function(req, res) {
    try {
        var param = req.body;
        var affectedRef = mongoose.mongo.BSONPure.ObjectID(param._id);
        var initiatorRef = mongoose.mongo.BSONPure.ObjectID(req.currentUser._id);
    } catch (e) {
        ServicesUtil.responseError(res, new ServerError(ServerError.PeopleNotExist));
        return;
    }

    async.waterfall([
    function(callbck) {
        // Validate existed relationship
        _getRPeopleFollowPeople(initiatorRef, affectedRef, function(err, r) {
            if (err) {
                callbck(err);
            } else if (r) {
                callbck(ServerError.AlreadyFollowPeople);
            } else {
                callbck(null);
            }
        });
    },
    function(callback) {
        // Create relationship
        new RPeopleFollowPeople({
            'initiatorRef' : initiatorRef,
            'affectedRef' : affectedRef
        }).save(callback);
    }], function(err) {
        if (err) {
            ServicesUtil.responseError(res, new ServerError(err));
        } else {
            res.end();
        }
    });
};

var _unfollow = function(req, res) {
    try {
        var param = req.body;
        var affectedRef = mongoose.mongo.BSONPure.ObjectID(param._id);
        var initiatorRef = mongoose.mongo.BSONPure.ObjectID(req.currentUser._id);
    } catch (e) {
        ServicesUtil.responseError(res, new ServerError(ServerError.PeopleNotExist));
        return;
    }

    async.waterfall([
    function(callback) {
        // Get relationship
        _getRPeopleFollowPeople(initiatorRef, affectedRef, callback);
    },
    function(relationship, callback) {
        // Remove relationship
        relationship.remove(callback);
    }], function(err) {
        if (err) {
            ServicesUtil.responseError(res, new ServerError(err));
        } else {
            res.end();
        }
    });
};

var _getRPeopleFollowPeople = function(initiatorRef, affectedRef, callback) {
    RPeopleFollowPeople.findOne({
        'initiatorRef' : initiatorRef,
        'affectedRef' : affectedRef
    }, callback);
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
