var mongoose = require('mongoose');
var async = require('async');
// Models
var People = require('../../model/peoples');

var ContextHelper = require('../helpers/ContextHelper');
var ServicesUtil = require('../servicesUtil');

module.exports.create = function(Model, initiatorRef, affectedRef, callback) {
    async.waterfall([
    function(callbck) {
        // Validate existed relationship
        Model.findOne({
            'initiatorRef' : initiatorRef,
            'affectedRef' : affectedRef
        }, function(err, r) {
            if (err) {
                callbck(err);
            } else if (r) {
                callbck(ServerError.AlreadyRelated);
            } else {
                callbck(null);
            }
        });
    },
    function(callback) {
        // Create relationship
        new Model({
            'initiatorRef' : initiatorRef,
            'affectedRef' : affectedRef
        }).save(callback);
    }], callback);
};

module.exports.remove = function(Model, initiatorRef, affectedRef, callback) {
    async.waterfall([
    function(callback) {
        // Get relationship
        Model.findOne({
            'initiatorRef' : initiatorRef,
            'affectedRef' : affectedRef
        }, callback);
    },
    function(relationship, callback) {
        // Remove relationship
        if (relationship) {
            relationship.remove(callback);
        } else {
            callback(ServerError.AlreadyUnrelated);
        }
    }], callback);
};

module.exports.queryPeoples = function(Model, criteria, pageNo, pageSize, peopleField, currentUser, callback) {
    async.waterfall([
    function(callback) {
        ServicesUtil.limitQuery(Model.find(criteria), pageNo, pageSize).exec(callback);
    },
    function(relationships, callback) {
        var _ids = [];
        relationships.forEach(function(r) {
            _ids.push(r[peopleField]);
        });
        People.find({
            '_id' : {
                '$in' : _ids
            }
        }, callback);
    },
    function(peoples, callback) {
        ContextHelper.followedByCurrentUser(currentUser, peoples, callback);
    }], callback);
};

