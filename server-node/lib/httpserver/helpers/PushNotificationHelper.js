var JPush = require('jpush-sdk');
var winston = require('winston');

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
PushNotificationHelper.MessageNewRecommandations = "倾秀精选搭配上新，看看吧";

PushNotificationHelper.CommandQuestSharingObjectiveComplete = "questSharingComplete";
PushNotificationHelper.CommandNewShowComments = "newShowComments";
PushNotificationHelper.CommandNewRecommandations= "newRecommandations";

PushNotificationHelper.push = function(registrationIDs, message, extras, callback) {
    client.push().setPlatform('ios', 'android')
        .setAudience(JPush.registration_id(registrationIDs))
        .setNotification(JPush.ios(message, null, null, false, extras), JPush.android(message, message, null, extras))
        .send(function(err, res) {
            if (err) {
                winston.error('show/comment push error', err);
            } else {
                winston.info('show/comment push success => error:[', err, '], res:[', res, ']');
            }
            if (callback) {
                callback(err, res);
            }

        });
};
