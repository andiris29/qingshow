var mongoose = require('mongoose');
var async = require('async');

var People = require('../dbmodels').People;
var jPushAudiences = require('../dbmodels').JPushAudience;

var RequestHelper = require('../helpers/RequestHelper');
var PushNotificationHelper = require('../helpers/PushNotificationHelper');

var BonusHelper = module.exports;

BonusHelper.createBonusViaTrade = function(trade, item, callback){
    async.waterfall([
    function(callback){
        People.findOne({
            _id : trade.promoterRef
        }).exec(function(err, people) {
            callback(err, people);
        });
    },
    function(people, callback) {
        people.bonuses = people.bonuses || [];
        people.bonuses.push({
            status : 0,
            money : (trade.totalFee * global.qsConfig.bonus.rate).toFixed(2),
            notes : '来自' + item.name + '的佣金',
            icon : item.thumbnail,
            trigger : {
                itemRef : item._id,
                tradeRef : trade._id
            }
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

                PushNotificationHelper.push([people], targets, PushNotificationHelper.MessageNewBonus, {
                    'command' : PushNotificationHelper.CommandNewBonus
                }, null);
            }
        });
        callback(null, people);
    }],function(error, people){
    callback(error, people);
    });
};

BonusHelper.createBonusViaForger = function(forger, promoterRef, item, callback){
    async.waterfall([
    function(callback){
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
            money : ((item.promoPrice * 0.9) * global.qsConfig.bonus.rate).toFixed(2),
            notes : '来自' + item.name + '的佣金',
            icon : item.thumbnail,
            trigger : {
                itemRef : item._id,
                forgerRef : forger
            }
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

                PushNotificationHelper.push([people], targets, PushNotificationHelper.MessageNewBonus, {
                    'command' : PushNotificationHelper.CommandNewBonus
                }, null);
            }
        });
        callback(null, people);
    }],function(error, people){
    callback(error, people);
    });
};
