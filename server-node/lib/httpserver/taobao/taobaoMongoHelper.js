var helper = module.exports;

var async = require('async');
var taobaoWeb = require('./taobaoWeb');


helper.crawlItemTaobaoInfo = function (item, callback) {
    async.waterfall([
        function (callback) {
            //Web Taobao Info
            taobaoWeb.item.getWebSkus(item, function (err, taobaoInfo) {
                item.taobaoInfo = taobaoInfo;
                callback();
            });
        }
    ], function (err) {
        if (err) {
            callback(err);
        } else {
            item.taobaoInfo = item.taobaoInfo || {};
            if (Object.keys(item.taobaoInfo).length === 0) {
                item.deactive = true;
            } else {
                delete item.deactive;
            }
            item.taobaoInfo.refreshTime = new Date();
            item.save(callback);
        }
    });
};