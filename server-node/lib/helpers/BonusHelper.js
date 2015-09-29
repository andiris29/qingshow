var mongoose = require('mongoose');
var async = require('async');

var People = require('../dbmodels').People;
var jPushAudiences = require('../dbmodels').JPushAudience;

var RequestHelper = require('../helpers/RequestHelper');
var NotificationHelper = require('../helpers/NotificationHelper');

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
        NotificationHelper.notify([people._id], NotificationHelper.MessageNewBonus, {
                    'command' : NotificationHelper.CommandNewBonus
                }, null);
        callback(null, people);
    }],function(error, people){
    callback(error, people);
    });
};

BonusHelper.createBonusViaForger = function(forger, fakeTrade, item, callback){
    async.waterfall([
    function(callback){
        People.findOne({
            _id : RequestHelper.parseId(fakeTrade.promoterRef)
        }).exec(function(err, people) {
            callback(err, people);
        });
    },
    function(people, callback) {
        people.bonuses = people.bonuses || [];
        people.bonuses.push({
            status : 0,
            money : (fakeTrade.actualPrice * global.qsConfig.bonus.rate).toFixed(2),
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
        NotificationHelper.notify([people._id], NotificationHelper.MessageNewBonus, {
            'command' : NotificationHelper.CommandNewBonus
        }, null);
        callback(null, people);
    }],function(error, people){
    callback(error, people);
    });
};
