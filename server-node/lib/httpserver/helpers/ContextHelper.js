var mongoose = require('mongoose');
var async = require('async');
// Models
var ShowComments = require('../../model/showComments');
var Show = require('../../model/shows');
var People = require('../../model/peoples');
var RPeopleFollowPeople = require('../../model/rPeopleFollowPeople');
var RPeopleFollowBrand = require('../../model/rPeopleFollowBrand');
var RPeopleLikeShow = require('../../model/rPeopleLikeShow');

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
    // __context.numFollowers
    var numFollowers = function(callback) {
        _numAssociated(peoples, RPeopleFollowPeople, 'targetRef', 'numFollowers', callback);
    };

    async.parallel([followedByCurrentUser, numShows, numFollowers], function(err) {
        callback(null, peoples);
    });
};

ContextHelper.appendShowContext = function(qsCurrentUserId, shows, callback) {
    shows = _prepare(shows);
    // __context.numComments
    var numComments = function(callback) {
        _numAssociated(shows, ShowComments, 'targetRef', 'numComments', callback);
    };
    // __context.numLike
    var numLike = function(callback) {
        _numAssociated(shows, RPeopleLikeShow, 'targetRef', 'numLike', callback);
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
    async.parallel([numComments, numLike, likedByCurrentUser, followedByCurrentUser], function(err) {
        callback(null, shows);
    });
};

ContextHelper.appendBrandContext = function(qsCurrentUserId, brands, callback) {
    brands = _prepare(brands);
    // __context.followedByCurrentUser
    var followedByCurrentUser = function(callback) {
        _rInitiator(RPeopleFollowBrand, qsCurrentUserId, brands, 'followedByCurrentUser', callback);
    };

    async.parallel([followedByCurrentUser], function(err) {
        callback(null, brands);
    });
};

var _prepare = function(models) {
    return models.map(function(model) {
        if (model.toJSON) {
            model = model.toJSON();
        }
        model.__context = {};
        return model;
    });
};

var _numAssociated = function(models, RModel, associateField, contextField, callback) {
    var tasks = models.map(function(model) {
        return function(callback) {
            var criteria = {};
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
    if (initiatorRef) {
        var tasks = models.map(function(model) {
            return function(callback) {
                RModel.findOne({
                    'initiatorRef' : initiatorRef,
                    'targetRef' : model._id
                }, function(err, relationship) {
                    model.__context[contextField] = Boolean(!err && relationship);
                    callback(null);
                });
            };
        });
        async.parallel(tasks, function(err) {
            callback(null, models);
        });
    } else {
        callback(null, models);
    }
};
