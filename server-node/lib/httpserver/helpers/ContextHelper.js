var mongoose = require('mongoose');
var async = require('async');
// Models
var People = require('../../model/peoples');
var RFollowPeople = require('../../model/rFollowPeople');

/**
 * ContextHelper
 *
 * Input models then output models with context
 */

module.exports.followedByCurrentUser = function(currentUser, peoples, callback) {
    if (currentUser) {
        var peopleRef = mongoose.mongo.BSONPure.ObjectID(currentUser._id);

        var followRefs = [], _idToIndex = {};
        peoples.forEach(function(people, index) {
            _idToIndex[people._id] = index;
            followRefs.push(people._id);
        });

        RFollowPeople.find({
            'peopleRef' : peopleRef,
            'followRef' : {
                '$in' : followRefs
            }
        }, function(err, relationships) {
            if (err) {
                callback(err);
            } else {
                relationships.forEach(function(r) {
                    var index = _idToIndex[r.followRef];
                    if (index !== undefined) {
                        var peopleJSON = peoples[index].toJSON();
                        peopleJSON.__context = {
                            'followedByCurrentUser' : true
                        };
                        peoples[index] = peopleJSON;
                    }
                });
                callback(null, peoples);
            }
        });
    } else {
        callback(null, peoples);
    }
};
