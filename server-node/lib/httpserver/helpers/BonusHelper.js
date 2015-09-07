var mongoose = require('mongoose');
var async = require('async');

var People = require('../../model/peoples');
var jPushAudiences = require('../../model/jPushAudiences');

var RequestHelper = require('../helpers/RequestHelper');
var PushNotificationHelper = require('../helpers/PushNotificationHelper');

var BonusHelper = module.exports;

BonusHelper.updateBonuse = function(promoterRef, trigger, money, name, callback) {
    async.waterfall([
    function(callback) {
        People.findOne({
            _id : promoterRef
        }).exec(function(err, people) {
            callback(err, people);
        });
    }, 
    function(people, callback) {
        people.bonuses = people.bonuses || [];
        people.bonuses.push({
            status : 0,
            money : (money / 100).toFixed(2),
            notes : '来自' + name + '的佣金',
            trigger : trigger
        });
        people.save(function(err, people) {
            callback(err, people);
        });
    },
    function(people, callback) {
        jPushAudiences.find({
            'peopleRef' : people._id
        }).exec(function(err, infos) {
            if (infos.length > 0) {
                var targets = [];
                infos.forEach(function(element) {
                    if (element.registrationId && element.registrationId.length > 0) {
                        targets.push(element.registrationId);
                    }
                });

                PushNotificationHelper.push(targets, PushNotificationHelper.MessageNewBonus, {
                    'command' : PushNotificationHelper.CommandNewBonus
                }, null);
            }
        });
        callback(null, people);
    }], function(error, people) {
        callback(error, people);
    });
};
