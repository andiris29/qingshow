var mongoose = require('mongoose');
var async = require('async');
// Models
var ShowComments = require('../../model/showComments');
//var PreviewComments = require('../../model/previewComments');
var Show = require('../../model/shows');
var People = require('../../model/peoples');
var RPeopleFollowPeople = require('../../model/rPeopleFollowPeople');
var RPeopleLikeShow = require('../../model/rPeopleLikeShow');
var RPeopleLikePreview = require('../../model/rPeopleLikePreview');

/**
 * ContextHelper
 *
 * Input models then output models with context
 */
var ContextHelper = module.exports;

ContextHelper.appendPeopleContext = function(qsCurrentUserId, peoples, callback) {
    peoples = _prepare(peoples);
    // __context.followedByCurrentUser
    var followedByCurrentUser = function(callback) {
        _rInitiator(RPeopleFollowPeople, qsCurrentUserId, peoples, 'followedByCurrentUser', callback);
    };
    // __context.numShows
    var numShows = function(callback) {
        _numAssociated(peoples, Show, 'modelRef', 'numShows', callback);
    };
    // __context.numFollowPeoples
    var numFollowPeoples = function(callback) {
        _numAssociated(peoples, RPeopleFollowPeople, 'initiatorRef', 'numFollowPeoples', callback);
    };
    // __context.numFollowers
    var numFollowers = function(callback) {
        _numAssociated(peoples, RPeopleFollowPeople, 'targetRef', 'numFollowers', callback);
    };

    async.parallel([followedByCurrentUser, numShows, numFollowBrands, numFollowPeoples, numFollowers], function(err) {
        callback(null, peoples);
    });
};

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
    // modedRef.__context.followedByCurrentUser
    var followedByCurrentUser = function(callback) {
        var peoples = shows.map(function(show) {
            return show.modelRef;
        });
        peoples = _prepare(peoples);
        _rInitiator(RPeopleFollowPeople, qsCurrentUserId, peoples, 'followedByCurrentUser', callback);
    };
    async.parallel([numComments, likedByCurrentUser, followedByCurrentUser], function(err) {
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
