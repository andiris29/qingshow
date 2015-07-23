var mongoose = require('mongoose');
var async = require('async');
var _ = require('underscore');

var People = require('../../model/peoples');
var jPushToPeople = require('../../model/jPushToPeople');

var ResponseHelper = require('../helpers/ResponseHelper');
var PushNotificationHelper = require('../helpers/PushNotificationHelper');

var notify = module.exports;

notify.newRecommandations = {
    'method' : 'post',
    'func' : function(req, res) {
        var group = req.body.group;
        
       
        async.waterfall([
        function(callback) {
            People.find({}).exec(callback);
        },
        function(peoples, callback) {
            var targets = _.filter(peoples, function(people) {
                var rate = people.weight / people.height;
                var type = null;
                 if (rate < 0.275) {
                    type = 'A1';
                } else if (rate < 0.315) {
                    type = 'A2';
                } else if (rate < 0.405) {
                    type = 'A3';
                } else {
                    type = 'A4';
                }

                return type == group;
            });

            var ids = [];
            targets.forEach(function(target) {
                ids.push(target._id);
            });
            callback(null, ids);
        },
        function(ids, callback) {
            jPushToPeople.find({
                peopleRef : {
                    '$in' : ids 
                }
            }).exec(function(err, infos) {
                callback(err, infos);
            });
        },
        function(targets, callback) {
            var registrationIDs = [];
            targets.forEach(function(target) {
                registrationIDs.push(target.registrationId);
            });
            PushNotificationHelper.push(registrationIDs, PushNotificationHelper.MessageNewRecommandations, {
                'command' : PushNotificationHelper.CommandNewRecommandations
            }, callback);
        }], 
        function(err) {
            ResponseHelper.response(res, err, null);
        });
    }
};

notify.questSharingObjectiveComplete = {
    'method' : 'post',
    'func' : function(req, res) {
        async.waterfall([
        function(callback) {
            People.find({
                'questSharing.status' : 1
            }).exec(function(err, peoples) {
                callback(err, peoples);
            });
        },
        function(peoples, callback) {
            var ids = [];
            peoples.forEach(function(people) {
                ids.push(people._id);
            });
            callback(null, ids);
        },
        function(ids, callback) {
            jPushToPeople.find({
                peopleRef : {
                    '$in' : ids
                }
            }).exec(function(err, infos) {
                callback(err, infos);
            });
        },
        function(peoples, callback) {
            var registrationIDs = [];
            peoples.forEach(function(target) {
                registrationIDs.push(target.registrationId);
            });
            PushNotificationHelper.push(registrationIDs, PushNotificationHelper.MessageQuestSharingObjectiveComplete, {
                'command' : PushNotificationHelper.CommandQuestSharingObjectiveComplete
            }, null);
            callback();
        }], 
        function(err) {
            ResponseHelper.response(res, err, null);
        });
    }
};
