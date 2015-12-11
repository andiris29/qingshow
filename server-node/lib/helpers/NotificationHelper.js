var JPush = require('jpush-sdk');
var winston = require('winston');
var _ = require('underscore');
var async = require('async');
var extend = require('util')._extend;

var People = require('../dbmodels').People;
var jPushAudiences = require('../dbmodels').JPushAudience;

// APP_KEY,Master_Key 
var JPushConfig = {
    Debug: {                // <-- HashMap的测试用 App Key
        AppKey : 'caa96ddbab44562ee6bb9d58',
        MasterKey : 'c1cd20800d855c7b751548a8'
    },
    Release: {              // APNS 生产证书
        AppKey : 'dad14285add5a5ade0fbfd81',
        MasterKey : 'd10901a237cddc3e1b0d1f63'
    }
};
var client = JPush.buildClient(JPushConfig.Release.AppKey, JPushConfig.Release.MasterKey);

var NotificationHelper = module.exports;

NotificationHelper.MessageNewShowComment = "您的搭配有新评论！";
NotificationHelper.MessageNewRecommandations = "最新的搭配已经推送给您，美丽怎能忍心被忽略，去看看吧！";
NotificationHelper.MessageTradeShipped = "您购买的宝贝已经向您狂奔而来，等着接收惊喜哟！";
NotificationHelper.MessageNewBonus = "您有{0}佣金入账啦，立即查看！";
NotificationHelper.MessageBonusWithdrawComplete = "您的账户成功提现{0}，请注意查看账户！";
NotificationHelper.MessageTradeRefundComplete = "款项已经退回您的支付账号，请查收。";

NotificationHelper.CommandNewShowComments = "newShowComments";
NotificationHelper.CommandNewRecommandations= "newRecommandations";
NotificationHelper.CommandTradeShipped = "tradeShipped";
NotificationHelper.CommandNewBonus = "newBonus";
NotificationHelper.CommandBonusWithdrawComplete = "bonusWithdrawComplete";
NotificationHelper.CommandTradeRefundComplete = "tradeRefundComplete";

NotificationHelper.notify = function(peoplesIds, message, extras, cb) {
    async.series([function(callback){
        NotificationHelper._push(peoplesIds, message, extras, function(err, res){
            callback(err, res);
        });
    }, function(callback){
        NotificationHelper._saveAsUnread(peoplesIds, extras, function(err){
            callback(err);
        });
    }], cb);
};

NotificationHelper._push = function(peoplesIds, message, extras, cb) {
    async.waterfall([function(callback){
        jPushAudiences.find({
            peopleRef : {
                '$in' : peoplesIds
            }
        }).exec(function(err, infos) {
            callback(err, infos);
        });
    }, function(infos, callback){
        var registrationIDs = [];
        infos.forEach(function(info) {
            registrationIDs.push(info.registrationId);
        });
        var sendTargets = _.filter(registrationIDs, function(registrationId) {
            return (registrationId && (registrationId.length > 0));
        });
        if (sendTargets.length) {
            client.push().setPlatform('ios', 'android')
            .setAudience(JPush.registration_id(sendTargets))
            .setNotification(JPush.ios(message, 'default', null, false, extras), JPush.android(message, message, null, extras))
            .setOptions(null, null, null, true, null)
            .send(function(err, res) {
                if (err) {
                    winston.error('Push error: ' + err);
                } else {
                    winston.info('Push success: ' + res);
                }
                if (callback) {
                    callback(err, res);
                }
            });
        } else {
            callback();
        }
    }], cb);
};

NotificationHelper.read = function(peoplesIds, criteria, callback) {
    People.update({
        '_id' : {
            '$in' : peoplesIds
        }
    }, {
        '$pull' : {
            'unreadNotifications' : criteria
        }
    }, {
        multi : true
    }, function(err, doc){
        callback(err, doc);  
    });
};

NotificationHelper._saveAsUnread = function(peoplesIds, extras, cb) {
    async.waterfall([function(callback){
        var criteria = {};
        for (var element in extras) {
            criteria['extra.' + element] = extras[element];
        }
        NotificationHelper.read(peoplesIds, criteria, function(err, peoples){
            callback(err, peoples);
        });
    }, function(peoples, callback){
        People.update({
            '_id' : {
                '$in' : peoplesIds
            }
        }, {
            $push : {
                'unreadNotifications' : {
                    'extra' : extras
                }
            }
        }, {
            multi : true
        }, function(err, doc){
            callback(err, doc);
        });
    }], cb);
};
