var mongoose = require('mongoose');
var async = require('async');
// Models
var ShowComments = require('../../model/showComments');
var People = require('../../model/peoples');
var RPeopleFollowPeople = require('../../model/rPeopleFollowPeople');
var RPeopleLikeShow = require('../../model/rPeopleLikeShow');

/**
 * ContextHelper
 *
 * Input models then output models with context
 */
var ContextHelper = module.exports;

ContextHelper.prepare = function(models) {
    return models.map(function(model) {
        if (model.toJSON) {
            model = model.toJSON();
        }
        model.__context = {};
        return model;
    });
};

ContextHelper.peopleFollowedByCurrentUser = function(currentUserId, peoples, callback) {
    _rInitiator(RPeopleFollowPeople, currentUserId, peoples, 'followedByCurrentUser', callback);
};

ContextHelper.brandFollowedByCurrentUser = function(currentUserId, peoples, callback) {
    _rInitiator(RPeopleFollowBrand, currentUserId, peoples, 'followedByCurrentUser', callback);
};

ContextHelper.showLikedByCurrentUser = function(currentUserId, shows, callback) {
    _rInitiator(RPeopleLikeShow, currentUserId, shows, 'likedByCurrentUser', callback);
};

ContextHelper.numShowComments = function(shows, callback) {
    _numTargeted(shows, ShowComments, 'numComments', callback);
};

ContextHelper.numLikeShow = function(shows, callback) {
    _numTargeted(shows, RPeopleLikeShow, 'numLike', callback);
};

var _numTargeted = function(models, RModel, contextField, callback) {
    var tasks = models.map(function(model) {
        return function(callback) {
            RModel.count({
                'targetRef' : model._id
            }, function(err, count) {
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
