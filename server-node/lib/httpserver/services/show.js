var mongoose = require('mongoose');
var async = require('async');
//model
var Show = require('../../model/shows');
var People = require('../../model/peoples');
var ShowComment = require('../../model/showComments');
//util
var MongoHelper = require('../helpers/MongoHelper');
var ContextHelper = require('../helpers/ContextHelper');
var RelationshipHelper = require('../helpers/RelationshipHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var RequestHelper = require('../helpers/RequestHelper');

var ServerError = require('../server-error');
var RPeopleLikeShow = require('../../model/rPeopleLikeShow');

var _query = function(req, res) {
    var _ids;
    async.waterfall([
    function(callback) {
        // Parser req
        try {
            _ids = RequestHelper.parseIds(req.queryString._ids);
            callback(null);
        } catch (err) {
            callback(ServerError.fromError(err));
        }
    },
    function(callback) {
        // Query & populate
        Show.find({
            '_id' : {
                '$in' : _ids
            }
        }).populate('modelRef').populate('itemRefs').exec(callback);
    },
    function(shows, callback) {
        // Populate nested references
        Show.populate(shows, {
            'path' : 'itemRefs.brandRef',
            'model' : 'brands'
        }, callback);
    },
    function(shows, callback) {
        // Append followed by current user
        ContextHelper.appendShowContext(req.qsCurrentUserId, shows, callback);
    }], function(err, shows) {
        ResponseHelper.response(res, err, {
            'shows' : shows
        });
    });

};
var _like = function(req, res) {
    try {
        var param = req.body;
        var targetRef = mongoose.mongo.BSONPure.ObjectID(param._id);
        var initiatorRef = mongoose.mongo.BSONPure.ObjectID(req.qsCurrentUserId);
    } catch (err) {
        ResponseHelper.response(res, ServerError.RequestValidationFail);
        return;
    }

    RelationshipHelper.create(RPeopleLikeShow, initiatorRef, targetRef, function(err) {
        ResponseHelper.response(res, err);
    });
};

var _unlike = function(req, res) {
    try {
        var param = req.body;
        var targetRef = mongoose.mongo.BSONPure.ObjectID(param._id);
        var initiatorRef = mongoose.mongo.BSONPure.ObjectID(req.qsCurrentUserId);
    } catch (e) {
        ResponseHelper.response(res, ServerError.RequestValidationFail);
        return;
    }

    RelationshipHelper.remove(RPeopleLikeShow, initiatorRef, targetRef, function(err) {
        ResponseHelper.response(res, err);
    });
};

var _queryComments = function(req, res) {
    var pageNo, pageSize, numTotal;
    var _id;
    async.waterfall([
    function(callback) {
        // Parse request
        try {
            var param = req.queryString;
            pageNo = parseInt(param.pageNo || 1), pageSize = parseInt(param.pageSize || 20);
            _id = mongoose.mongo.BSONPure.ObjectID(param._id);
            callback(null);
        } catch(err) {
            callback(ServerError.fromError(err));
        }
    },
    function(callback) {
        // Query
        var criteria = {
            'targetRef' : _id,
            'delete' : null
        };
        MongoHelper.queryPaging(ShowComment.find(criteria).sort({
            'create' : 1
        }).populate('authorRef').populate('atRef'), ShowComment.find(criteria), pageNo, pageSize, function(err, count, showComments) {
            numTotal = count;
            callback(err, showComments);
        });
    }], function(err, showComments) {
        // Response
        ResponseHelper.responseAsPaging(res, err, {
            'showComments' : showComments
        }, pageSize, numTotal);
    });
};

var _comment = function(req, res) {
    try {
        var param = req.body;
        var targetRef = mongoose.mongo.BSONPure.ObjectID(param._id);
        var atRef = mongoose.mongo.BSONPure.ObjectID(param._atId);
        var comment = param.comment;
    } catch (err) {
        ResponseHelper.response(res, err);
        return;
    }
    async.waterfall([
    function(callback) {
        var showComment = new ShowComment({
            'targetRef' : targetRef,
            'atRef' : atRef,
            'authorRef' : req.qsCurrentUserId,
            'comment' : comment
        });
        showComment.save(function(err) {
            callback();
        });
    }], function(err) {
        ResponseHelper.response(res, err);
    });

};

var _deleteComment = function(req, res) {
    try {
        var param = req.body;
        var _id = mongoose.mongo.BSONPure.ObjectID(param._id);
    } catch (err) {
        ResponseHelper.response(res, err);
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
    }], function(err) {
        ResponseHelper.response(res, err);
    });
};

module.exports = {
    'query' : {
        'method' : 'get',
        'func' : _query
    },
    'like' : {
        method : 'post',
        func : _like,
        permissionValidators : ['loginValidator']
    },
    'unlike' : {
        method : 'post',
        func : _unlike,
        permissionValidators : ['loginValidator']
    },
    'queryComments' : {
        method : 'get',
        func : _queryComments
    },
    'comment' : {
        method : 'post',
        func : _comment,
        permissionValidators : ['loginValidator']
    },
    'deleteComment' : {
        method : 'post',
        func : _deleteComment,
        permissionValidators : ['loginValidator']
    }
};
