var argv = require('minimist')(process.argv.slice(2));
var async = require('async'), _ = require('underscore');
var request = require('request');
var Iconv = require('iconv').Iconv;

// Log
var winston = require('winston'), fs = require('fs'), path = require('path');
var folderLogs = path.join(__dirname, '../logs');
if (!fs.existsSync(folderLogs)) {
    fs.mkdirSync(folderLogs);
}
winston.add(winston.transports.DailyRotateFile, {
    'filename' : path.join(folderLogs, '/winston.log')
});
// Handle uncaught exceptions
process.on('uncaughtException', function(err) {
    winston.info(new Date().toString() + ': uncaughtException');
    winston.info(err);
    winston.info('\t' + err.stack);
});

var appServerURL = argv['app-server-url'];
appServerURL = "http://" + appServerURL + "/services"
var _next = function(index) {
    var logs = [];
    async.waterfall([
    function (callback) {
        request.get({
            'url' : appServerURL + '/goblin/queryItems',
            'qs' : {
                'pageNo' : index,
                'pageSize' : 1
            }
        }, function (err, response, body) {
            if (err) {
                callback(err);
                return;
            }
            var retObj = JSON.parse(body);
            try {
                callback(null, retObj.data.items[0]);
            } catch (e) {
                callback(e);
            }
        })
    },
    function(item, callback) {
        logs.push('item._id=' + item._id);
        callback(null, item.source);
    },
    function(source, callback) {
        /*
        * http://detail.tmall.com/item.htm?spm=a1z10.4.w5003-9301197547.23.qjlW0g&id=40952601693&rn=2b5acab447292dedd6c7bc1d2e9a5387&abbucket=7&scene=taobao_shop
        * http://item.taobao.com/item.htm?spm=2013.1.w5734072-1564537832.10.Vg0Rmz&id=42888614090
        * */
        var tmallRegex = /http:\/\/\w*\.tmall\.com/;
        var taobaoRegex = /http:\/\/\w*\.taobao\.com/;
        var idRegex = /id=(\d*)/
        var idComp = source.match(idRegex);
        var tbItemId = null;
        if (idComp && idComp.length > 1) {
            tbItemId = idComp[1];
        } else {
            // TODO handle parse id error
        }
        if (source.match(tmallRegex)) {
            // TODO Implement tmall
            _tmall(tbItemId, callback);
        } else if (source.match(taobaoRegex)) {
            _taobao(tbItemId, callback);
        } else {
            // TODO handle error
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
_next(1);

var _taobao = function(tbItemId, callback) {
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
            try {
                var g_config = {
                    'vdata' : {}
                };
                eval(new Iconv('gbk', 'utf-8').convert(new Buffer(body, 'binary')).toString());

                var price, promoPrice;
                // TODO Parse g_config.PromoData to find the item.brandDiscountInfo.price
                // TODO Parse g_config.Price to find the price
                callback(null, price, promoPrice);
            } catch(err) {
                callback(err);
            }
        }
    });
};

var _tmall = function(tbItemId, callback) {
    var tbItemId = '42550435119';
    request.get({
        'url' : 'http://mdskip.taobao.com/core/initItemDetail.htm?callback=setMdskip&itemId=' + tbItemId,
        'headers' : {
            'referer' : 'http://detail.tmall.com/item.htm?id=' + tbItemId,
            'accept-language' : 'en,en-US;q=0.8,zh-CN;q=0.6,zh;q=0.4'
        },
        'encoding' : 'binary'
    }, function(err, response, body) {
        if (err) {
            callback(err);
        } else {
            try {

                var setMdskip = function(object) {
                    var price, promoPrice;
                    // TODO Parse g_config.PromoData to find the item.brandDiscountInfo.price
                    // TODO Parse g_config.Price to find the price
                    console.log(object.defaultModel.deliveryDO.deliveryAddress);
                    //object.defaultModel.itemPriceResultDO.priceInfo
                    callback(null, price, promoPrice);
                };
                eval(new Iconv('gbk', 'utf-8').convert(new Buffer(body, 'binary')).toString());
                console.log(g_config);
            } catch(err) {
                callback(err);
            }
        }
    });
};
