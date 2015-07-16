var JPush = require('jpush-sdk');

// APP_KEY,Master_Key 
var JPushConfig = {
    Debug: {                // <-- HashMap的测试用 App Key
        AppKey : 'caa96ddbab44562ee6bb9d58',
        MasterKey : 'c1cd20800d855c7b751548a8'
    },
    Develop: {              // APNS 开发证书
        AppKey : 'dad14285add5a5ade0fbfd81',
        MasterKey : 'd10901a237cddc3e1b0d1f63'
    },
    Release: {              // APNS 生产证书
        AppKey : '',
        MasterKey : ''
    }
};
var client = JPush.buildClient(JPushConfig.Debug.AppKey, JPushConfig.Debug.MasterKey);

var PushNotificationHelper = module.exports;

PushNotificationHelper.push = function(registrationIDs, message, callback) {
    client.push().setPlatform('ios', 'android')
        .setAudience(JPush.registration_id(registrationIDs))
        .setNotification(JPush.ios(message), JPush.android(message, message))
        .sned(callback);
};
