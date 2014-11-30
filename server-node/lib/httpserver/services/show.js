var mongoose = require('mongoose');
var async = require('async');
//model
var Show = require('../../model/shows');
var People = require('../../model/peoples');
var ShowComment = require('../../model/showComments');
//util
var RelationshipHelper = require('../helpers/RelationshipHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var ServerError = require('../server-error');
var ServicesUtil = require('../servicesUtil');
var RPeopleLikeShow = require('../../model/rPeopleLikeShow');

var _like = function(req, res) {
    try {
        var param = req.body;
        var affectedRef = mongoose.mongo.BSONPure.ObjectID(param._id);
        var initiatorRef = mongoose.mongo.BSONPure.ObjectID(req.qsCurrentUserId);
    } catch (e) {
        ServicesUtil.responseError(res, new ServerError(ServerError.RequestValidationFail));
        return;
    }

    RelationshipHelper.create(RPeopleLikeShow, initiatorRef, affectedRef, ResponseHelper.generateGeneralCallback(res));
};

var _unlike = function(req, res) {
    try {
        var param = req.body;
        var affectedRef = mongoose.mongo.BSONPure.ObjectID(param._id);
        var initiatorRef = mongoose.mongo.BSONPure.ObjectID(req.qsCurrentUserId);
    } catch (e) {
        ServicesUtil.responseError(res, new ServerError(ServerError.RequestValidationFail));
        return;
    }

    RelationshipHelper.remove(RPeopleLikeShow, initiatorRef, affectedRef, ResponseHelper.generateGeneralCallback(res));
};

var _queryComments = function(req, res) {
    try {
        var param = req.queryString;
        var _id = mongoose.mongo.BSONPure.ObjectID(param.showId);
        var pageNo = parseInt(param.pageNo || 1), pageSize = parseInt(param.pageSize || 10);
    } catch (e) {
        ServicesUtil.responseError(res, new ServerError(ServerError.RequestValidationFail));
        return;
    }

    var buildQuery = function() {
        return ShowComment.find({
            'targetRef' : _id,
            'delete' : null
        });
    };
    var additionFunc = function(query) {
        query.sort({
            'create' : 1
        }).populate('peopleRef');
    };
    var commentDataGenFunc = function(data) {
        return {
            'showComments' : data
        };
    };
    ServicesUtil.sendSingleQueryToResponse(res, buildQuery, additionFunc, commentDataGenFunc, pageNo, pageSize);
};

var _comment = function(req, res) {
    try {
        var param = req.body;
        var targetRef = mongoose.mongo.BSONPure.ObjectID(param._id);
        var atRef = mongoose.mongo.BSONPure.ObjectID(param._atId);
        var comment = param.comment;
    } catch (e) {
        ServicesUtil.responseError(res, e);
        return;
    }
    async.waterfall([
    function(callback) {
        var comment = new ShowComment({
            'targetRef' : targetRef,
            'atRef' : atRef,
            'authorRef' : req.qsCurrentUserId,
            'comment' : comment
        });
        comment.save(function(err) {
            callback();
        });
    }], ResponseHelper.generateGeneralCallback(res));

};

var _deleteComment = function(req, res) {
    try {
        var param = req.body;
        var _id = mongoose.mongo.BSONPure.ObjectID(param._id);
    } catch (e) {
        ServicesUtil.responseError(res, e);
        return;
    }
    async.waterfall([
    function(callback) {
        ShowComment.findOne({
            '_id' : _id,
            'authorRef' : req.qsCurrentUserId,
            'delete' : null
        }, callback);
    },
    function(comment, callback) {
        if (comment) {
            comment.set('delete', new Date());
            comment.save(function(err) {
                callback();
            });
        } else {
            callback();
        }
    }], ResponseHelper.generateGeneralCallback(res));
};

module.exports = {
    'like' : {
        method : 'post',
        func : _like,
        needLogin : true
    },
    'unlike' : {
        method : 'post',
        func : _unlike,
        needLogin : true
    },
    'queryComments' : {
        method : 'get',
        func : _queryComments,
        needLogin : false
    },
    'comment' : {
        method : 'post',
        func : _comment,
        needLogin : true
    },
    'deleteComment' : {
        method : 'post',
        func : _deleteComment,
        needLogin : true
    }
};
