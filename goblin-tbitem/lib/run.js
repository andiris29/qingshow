var argv = require('minimist')(process.argv.slice(2));
var async = require('async'), _ = require('underscore');
var request = require('request');
var Iconv = require('iconv').Iconv;

// Log
var winston = require('winston');
winston.add(winston.transports.DailyRotateFile, {
    'filename' : require('path').join(__dirname, 'winston.log')
});
// Handle uncaught exceptions
process.on('uncaughtException', function(err) {
    winston.info(new Date().toString() + ': uncaughtException');
    winston.info(err);
    winston.info('\t' + err.stack);
});

var appServerURL = argv['app-server-url'];

var _next = function(index) {
    var logs = [];
    async.waterfall([
    function(callback) {
        // TODO: Query item via app server
        item = {};
        logs.push('item._id=' + item._id);
        callback(null, item.source);
    },
    function(source, callback) {
        // TODO Parse source
        if (true) {
            _taobao(source, callback);
        } else {
            // TODO Implement tmall
            _tmall(source, callback);
        }
    },
    function(price, promoPrice, callback) {
        // TODO: Save price to item.price via app server
        // TODO: Save promoPrice to item.brandDiscountInfo.price via app server
        // logs.push('saved');
        callback();
    }], function(err) {
        if (err) {
            if (err === 'complete') {
                winston.info('all complete');
                process.exit();
            } else {
                logs.push('fail');
                winston.info(logs.join(' '));
                winston.info(err);
            }
        } else {
            logs.push('success');
            winston.info(logs.join(' '));

            // Call next after 1s, avoid to be blocked by taobao
            index++;
            _.delay(function() {
                _next(index);
            }, 1000);
        }
    });
};
_next(0);

var _taobao = function(source, callback) {
    // TODO Parse tbItemId from source
    var tbItemId = '41449399566';
    request.get({
        'url' : 'http://detailskip.taobao.com/json/sib.htm?p=1&itemId=' + tbItemId,
        'headers' : {
            'referer' : 'http://item.taobao.com/item.htm?id=' + tbItemId,
            'accept-language' : 'en,en-US;q=0.8,zh-CN;q=0.6,zh;q=0.4'
        },
        'encoding' : 'binary'
    }, function(err, response, body) {
        if (err) {
            callback(err);
        } else {
            var g_config = {
                'vdata' : {}
            };
            try {
                eval(new Iconv('gbk', 'utf-8').convert(new Buffer(body, 'binary')).toString());
                // TODO Parse g_config.PromoData to find the item.brandDiscountInfo.price
                // TODO Parse g_config.Price to find the price
                winston.info(JSON.stringify(g_config, null, 4));
                callback(null, price, promoPrice);
            } catch(err) {
                callback(err);
            }
        }
    });
};

var _tmall = function(tbItemId, callback) {
    // TODO Implement
};
