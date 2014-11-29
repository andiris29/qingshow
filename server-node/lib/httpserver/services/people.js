var mongoose = require('mongoose');
var async = require('async');
//model
var People = require('../../model/peoples');
var RFollowPeople = require('../../model/rFollowPeople');
//util
var ContextHelper = require('../helpers/ContextHelper');
var ServerError = require('../server-error');
var ServicesUtil = require('../servicesUtil');

var _queryModels = function(req, res) {
    var param = req.queryString;
    var pageNo = param.pageNo || 1;
    var pageSize = param.pageSize || 10;

    function buildQuery() {
        return People.find({
            'roles' : 1
        });
    }
    function modelDataGenFunc(data) {
        return {
            'peoples' : data
        };
    }
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
// Request
//  _id string ObjectId in peoples
var _follow = function(req, res) {
    try {
        var param = req.body;
        var followRef = mongoose.mongo.BSONPure.ObjectID(param._id);
        var peopleRef = mongoose.mongo.BSONPure.ObjectID(req.currentUser._id);
    } catch (e) {
        ServicesUtil.responseError(res, new ServerError(ServerError.PeopleNotExist));
        return;
    }

    var validateAlreadyFollowed = function(callbck) {
        _getRFollowPeople(peopleRef, followRef, function(err, r) {
            if (err) {
                callbck(err);
            } else if (r) {
                callbck(ServerError.AlreadyFollowPeople);
            } else {
                callbck(null);
            }
        });
    };
    async.parallel([validateAlreadyFollowed], function(err, results) {
        if (err) {
            ServicesUtil.responseError(res, new ServerError(err));
        } else {
            // Follow
            new RFollowPeople({
                'peopleRef' : peopleRef,
                'followRef' : followRef
            }).save(function(err, r) {
                if (err) {
                    ServicesUtil.responseError(res, new ServerError(err));
                } else {
                    res.end();
                }
            });
        }
    });
};

var _unfollow = function(req, res) {
    try {
        var param = req.body;
        var followRef = mongoose.mongo.BSONPure.ObjectID(param._id);
        var peopleRef = mongoose.mongo.BSONPure.ObjectID(req.currentUser._id);
    } catch (e) {
        ServicesUtil.responseError(res, new ServerError(ServerError.PeopleNotExist));
        return;
    }
    _getRFollowPeople(peopleRef, followRef, function(err, r) {
        if (err) {
            ServicesUtil.responseError(res, new ServerError(err));
        } else if (r) {
            r.remove(function(err) {
                if (err) {
                    ServicesUtil.responseError(res, new ServerError(err));
                } else {
                    res.end();
                }
            });
        } else {
            res.end();
        }
    });
};

var _getRFollowPeople = function(peopleRef, followRef, callback) {
    RFollowPeople.findOne({
        'peopleRef' : peopleRef,
        'followRef' : followRef
    }, callback);
};

module.exports = {
    'queryModels' : {
        method : 'get',
        func : _queryModels
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
