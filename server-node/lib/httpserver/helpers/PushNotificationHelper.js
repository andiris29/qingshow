var JPush = require('jpush-sdk');
var winston = require('winston');
var _ = require('underscore');

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
PushNotificationHelper.MessageTradeInitialized = "您申请的折扣已经成功啦，别让宝贝飞了，快块来付款吧！";
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

PushNotificationHelper.push = function(registrationIDs, message, extras, callback) {
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
};
