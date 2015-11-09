var mongoose = require('mongoose');
var async = require('async');

var People = require('../dbmodels').People;
var Show = require('../dbmodels').Show;
var jPushAudiences = require('../dbmodels').JPushAudience;

var RequestHelper = require('../helpers/RequestHelper');
var NotificationHelper = require('../helpers/NotificationHelper');

var BonusHelper = module.exports;

BonusHelper.createBonus = function(trade, item, cb){
    async.waterfall([
    function(callback){
        Show.find({
            itemRefs : {
                '$all' : [item._id]
            },
            ownerRef : {
                '$ne' : trade.promoterRef
            }
        }).distinct('ownerRef', function(err, refs){
            console.log(refs);
            callback(err, refs.slice(0, global.qsConfig.bonus.participants.count));
        })
    }, function(peopleRefs, callback) {
        var money = Math.round(Math.max(0.01, trade.totalFee * global.qsConfig.bonus.participants.rate) * 100) / 100;
        _createViaItem(peopleRefs, item, money, function(err){
            callback(null, peopleRefs);
        });
    }, function(peopleRefs, callback){
        var money = Math.round(Math.max(0.01, trade.totalFee * global.qsConfig.bonus.rate) * 100) / 100;
        _createViaTrade(trade, money, peopleRefs, callback);
    }],function(error, people){
        cb(error, people);
    });
};

var _createViaTrade = function(trade, money, participants, cb){
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
            money : money,
            notes : '来自' + trade.itemSnapshot.name + '的佣金',
            icon : trade.itemSnapshot.thumbnail,
            participants : participants,
            trigger : {
                itemRef : trade.itemRef,
                tradeRef : trade._id
            }
        });
        people.save(function(err, people) {
            callback(err, people);
        });
    },
    function(people, callback) {
        var message = NotificationHelper.MessageBonusWithdrawComplete.replace(/\{0\}/g, money);
        NotificationHelper.notify([people._id], message, {
                    'command' : NotificationHelper.CommandNewBonus
                }, null);
        callback(null, people);
    }],function(error, people){
        cb(error, people);
    });
}

var _createViaItem = function(peopleRefs, item, money, cb){
    async.waterfall([function(callback){
        People.update({
            _id : {
                '$in' : peopleRefs
            }
        },{
            '$push' : {
                bonuses : {
                    status : 0,
                    money : money,
                    notes : '来自' + item.name + '的佣金',
                    icon : item.thumbnail,
                    trigger : {
                        itemRef : item._id
                    }
                }
            } 
        },{
            multi: true 
        }, callback);
    }, function(doc, callback){
        var message = NotificationHelper.MessageBonusWithdrawComplete.replace(/\{0\}/g, money);
        NotificationHelper.notify(peopleRefs, message, {
            'command' : NotificationHelper.CommandNewBonus
        }, null);
        callback(null, doc);
    }], function(err, doc){
        cb(err);
    })
}
