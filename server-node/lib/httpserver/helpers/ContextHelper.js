var mongoose = require('mongoose');
var async = require('async');
// Models
var People = require('../../model/peoples');
var RPeopleFollowPeople = require('../../model/rPeopleFollowPeople');

/**
 * ContextHelper
 *
 * Input models then output models with context
 */

module.exports.followedByCurrentUser = function(currentUser, peoples, callback) {
    _affectedByCurrentUser(RPeopleFollowPeople, currentUser, peoples, 'followedByCurrentUser', callback);
};

var _affectedByCurrentUser = function(RModel, currentUser, models, contextField, callback) {
    if (currentUser) {
        var initiatorRef = mongoose.mongo.BSONPure.ObjectID(currentUser._id);

        var affectedRefs = [], _idToIndex = {};
        models.forEach(function(people, index) {
            _idToIndex[people._id] = index;
            affectedRefs.push(people._id);
        });

        RModel.find({
            'initiatorRef' : initiatorRef,
            'affectedRef' : {
                '$in' : affectedRefs
            }
        }, function(err, relationships) {
            if (err) {
                callback(err);
            } else {
                relationships.forEach(function(r) {
                    var index = _idToIndex[r.affectedRef];
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
