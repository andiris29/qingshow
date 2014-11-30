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

module.exports.followedByCurrentUser = function(currentUserId, peoples, callback) {
    _rTargetedByCurrentUser(RPeopleFollowPeople, currentUserId, peoples, 'followedByCurrentUser', callback);
};

module.exports.likedByCurrentUser = function(currentUserId, shows, callback) {
    _rTargetedByCurrentUser(RPeopleLikeShow, currentUserId, shows, 'likedByCurrentUser', callback);
};

var _rTargetedByCurrentUser = function(RModel, currentUserId, models, contextField, callback) {
    if (currentUserId) {
        var initiatorRef = mongoose.mongo.BSONPure.ObjectID(currentUserId);

        var targetRefs = [], _idToIndex = {};
        models.forEach(function(people, index) {
            _idToIndex[people._id] = index;
            targetRefs.push(people._id);
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
