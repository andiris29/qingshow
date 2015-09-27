var JPush = require('jpush-sdk');
var winston = require('winston');
var _ = require('underscore');
var async = require('async');

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

var PushNotificationHelper = module.exports;

PushNotificationHelper.MessageQuestSharingObjectiveComplete = "恭喜您！完成倾秀夏日季搭配活动任务！点击此处领奖吧～";
PushNotificationHelper.MessageNewShowComment = "您的搭配有新评论！";
PushNotificationHelper.MessageNewRecommandations = "最新的搭配已经推送给您，美丽怎能忍心被忽略，去看看吧！";
PushNotificationHelper.MessageQuestSharingProgress = "您还需要{0}个小伙伴助力即可获取大奖，继续加油吧！";
PushNotificationHelper.MessageTradeInitialized = "您申请的折扣已经成功啦，别让宝贝飞了，快来付款吧！";
PushNotificationHelper.MessageTradeShipped = "您购买的宝贝已经向您狂奔而来，等着接收惊喜哟！";
PushNotificationHelper.MessageItemPriceChanged = "您申请的折扣有最新信息，不要错过哦！";
PushNotificationHelper.MessageNewBonus = "您有一笔佣金入账啦，立即查看！";
PushNotificationHelper.MessageBonusWithdrawComplete = "您的账户成功提现{0}，请注意查看账户！";

PushNotificationHelper.CommandQuestSharingObjectiveComplete = "questSharingObjectiveComplete";
PushNotificationHelper.CommandNewShowComments = "newShowComments";
PushNotificationHelper.CommandNewRecommandations= "newRecommandations";
PushNotificationHelper.CommandQuestSharingProgress = "questSharingProgress";
PushNotificationHelper.CommandTradeInitialized = "tradeInitialized";
PushNotificationHelper.CommandTradeShipped = "tradeShipped";
PushNotificationHelper.CommandItemExpectablePriceUpdated = "itemExpectablePriceUpdated";
PushNotificationHelper.CommandNewBonus = "newBonus";
PushNotificationHelper.CommandBonusWithdrawComplete = "bonusWithdrawComplete";

PushNotificationHelper.notify = function(peoplesIds, message, extras, cb) {
    async.series([function(callback){
        PushNotificationHelper._push(peoplesIds, message, extras, function(err, res){
            callback(err, res);
        })
    }, function(callback){
        var addition = {};
        for(var element in extras){
            if (element !== 'command') {
                addition[element] = extras[element]
            }
        }
        PushNotificationHelper._saveAsUnread(peoplesIds, extras.command, addition, function(err){
            callback(err);
        })
    }], cb);
}

PushNotificationHelper._push = function(peoplesIds, message, extras, cb) {
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
    }], cb)
}

PushNotificationHelper._saveAsUnread = function(peoplesIds, cmd, addition, cb) {
    async.waterfall([function(callback){
        People.find({
            _id : {
                '$in' : peoplesIds
            }
        }).exec(function(err, peoples){
            callback(err, peoples)
        })
    }, function(peoples, callback){
        if (peoples && peoples.length > 0) {
            peoples.forEach(function(people){
                people.unreadNotifications = people.unreadNotifications || [];
                var extra = {};
                for(var element in addition){
                    extra[element] = addition[element]
                }
                extra['cmd'] = cmd;
                people.unreadNotifications.push({
                    'extra' : extra
                });
                people.save(function(error, people){
                    callback(error);
                });
            });   
        }else {
            callback()
        }
    }], cb)
}
