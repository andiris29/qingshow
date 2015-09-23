var mongoose = require('mongoose');
var async = require('async');
// Models
var People = require('../models').People;
var errors = require('../errors');
var ContextHelper = require('../helpers/ContextHelper');

module.exports.create = function(Model, initiatorRef, targetRef, callback) {
    async.waterfall([
    function(callbck) {
        // Validate existed relationship
        Model.findOne({
            'initiatorRef' : initiatorRef,
            'targetRef' : targetRef
        }, function(err, r) {
            if (err) {
                callbck(err);
            } else if (r) {
                callbck(errors.AlreadyRelated);
            } else {
                callbck(null);
            }
        });
    },
    function(callback) {
        // Create relationship
        new Model({
            'initiatorRef' : initiatorRef,
            'targetRef' : targetRef
        }).save(callback);
    }], callback);
};

module.exports.remove = function(Model, initiatorRef, targetRef, callback) {
    async.waterfall([
    function(callback) {
        // Get relationship
        Model.findOne({
            'initiatorRef' : initiatorRef,
            'targetRef' : targetRef
        }, callback);
    },
    function(relationship, callback) {
        // Remove relationship
        if (relationship) {
            relationship.remove(callback);
        } else {
            callback(errors.AlreadyUnrelated);
        }
    }], callback);
};

