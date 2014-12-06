var mongoose = require('mongoose');
var async = require('async');
// Models
var People = require('../../model/peoples');
var RPeopleFollowPeople = require('../../model/rPeopleFollowPeople');
var RPeopleLikeShow = require('../../model/rPeopleLikeShow');

/**
 * ContextHelper
 *
 * Input models then output models with context
 */

module.exports.followedByCurrentUser = function(Model, currentUserId, peoples, callback) {
    _rTargetedByCurrentUser(Model, currentUserId, peoples, 'followedByCurrentUser', callback);
};

module.exports.likedByCurrentUser = function(currentUserId, shows, callback) {
    _rTargetedByCurrentUser(RPeopleLikeShow, currentUserId, shows, 'likedByCurrentUser', callback);
};

var _rTargetedByCurrentUser = function(RModel, initiatorRef, models, contextField, callback) {
    if (initiatorRef) {
        var targetRefs = [], _idToIndex = {};
        models.forEach(function(model, index) {
            _idToIndex[model._id] = index;
            targetRefs.push(model._id);
        });

        RModel.find({
            'initiatorRef' : initiatorRef,
            'targetRef' : {
                '$in' : targetRefs
            }
        }, function(err, relationships) {
            if (err) {
                callback(err);
            } else {
                relationships.forEach(function(r) {
                    var index = _idToIndex[r.targetRef];
                    if (index !== undefined) {
                        var peopleJSON = models[index].toJSON();
                        peopleJSON.__context = {};
                        peopleJSON.__context[contextField] = true;
                        models[index] = peopleJSON;
                    }
                });
                callback(null, models);
            }
        });
    } else {
        callback(null, models);
    }
};
