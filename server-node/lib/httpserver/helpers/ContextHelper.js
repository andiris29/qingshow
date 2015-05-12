var mongoose = require('mongoose');
var async = require('async');
// Models
var ShowComments = require('../../model/showComments');
var RPeopleLikeShow = require('../../model/rPeopleLikeShow');
var RPeopleLikePreview = require('../../model/rPeopleLikePreview');
var RPeopleShareShow = require('../../model/rPeopleShareShow');

var RPeopleLikeItem = require('../../model/rPeopleLikeItem');

/**
 * ContextHelper
 *
 * Input models then output models with context
 */
var ContextHelper = module.exports;


ContextHelper.appendShowContext = function(qsCurrentUserId, shows, callback) {
    shows = _prepare(shows);
    // __context.numComments
    var numComments = function(callback) {
        _numAssociated(shows, ShowComments, 'targetRef', 'numComments', callback);
    };
    // __context.likedByCurrentUser
    var likedByCurrentUser = function(callback) {
        _rInitiator(RPeopleLikeShow, qsCurrentUserId, shows, 'likedByCurrentUser', callback);
    };
    // __context.sharedByCurrentUser
    var sharedByCurrentUser = function(callback) {
        _rInitiator(RPeopleShareShow, qsCurrentUserId, shows, 'sharedByCurrentUser', callback);
    };
    // modedRef.__context.followedByCurrentUser
    async.parallel([numComments, likedByCurrentUser, sharedByCurrentUser], function (err) {
        callback(null, shows);
    });
};

ContextHelper.appendPreviewContext = function(qsCurrentUserId, previews, callback) {
    previews = _prepare(previews);
    // __context.numComments
    var numComments = function(callback) {
        _numAssociated(previews, PreviewComments, 'targetRef', 'numComments', callback);
    };
    // __context.likedByCurrentUser
    var likedByCurrentUser = function(callback) {
        _rInitiator(RPeopleLikePreview, qsCurrentUserId, previews, 'likedByCurrentUser', callback);
    };
    async.parallel([numComments, likedByCurrentUser], function(err) {
        callback(null, previews);
    });
};

var _prepare = function(models) {
    return models.filter(function(model) {
        return model;
    }).map(function(model) {
        model.__context = model.__context || {};
        return model;
    });
};

var _numAssociated = function(models, RModel, associateField, contextField, callback) {
    var tasks = models.map(function(model) {
        return function(callback) {
            var criteria = {
                'delete' : null
            };
            criteria[associateField] = model._id;
            RModel.count(criteria, function(err, count) {
                model.__context[contextField] = count || 0;
                callback(null);
            });
        };
    });
    async.parallel(tasks, function(err) {
        callback(null, models);
    });
};

var _rInitiator = function(RModel, initiatorRef, models, contextField, callback) {
    var tasks = models.map(function(model) {
        return function(callback) {
            if (initiatorRef) {
                RModel.findOne({
                    'initiatorRef' : initiatorRef,
                    'targetRef' : model._id
                }, function(err, relationship) {
                    model.__context[contextField] = Boolean(!err && relationship);
                    callback();
                });
            } else {
                model.__context[contextField] = false;
                callback();
            }
        };
    });
    async.parallel(tasks, function(err) {
        callback(null, models);
    });
};
