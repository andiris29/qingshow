var winston = require('winston');
var async = require('async');
var iconv = require('iconv-lite');
var request = require('request');
var cheerio = require('cheerio');

var URLParser = require('../URLParser');

var JamyWebItem = module.exports;

JamyWebItem.getSkus = function(source, callback) {
    async.waterfall([function(callback) {
        callback(null, source);
    }, function(source, callback) {
        var get = request.get({
            'url' : source,
            'encoding' : 'binary'
        }, function(err, response, body) {
            if (err) {
                callback(err);
            } else {
                var html = iconv.decode(new Buffer(body, 'binary'), 'UTF-8');
                var $ = cheerio.load(html);

                var jamyInfo = {};

                var price = $('.prc-area-cny').text().split("￥");
                jamyInfo.promo_price = price[price.length - 1];
                jamyInfo.price = jamyInfo.promo_price;

                var skuProperties = [];
                var retStr = "颜色";
                $('select').first().children('option').each(function(i, elm) {
                    retStr = retStr + ":" + $(this).text();
                });

                skuProperties.push(retStr);

                retStr = "尺码";
                $('select').last().children('option').each(function(i, elm) {
                    retStr = retStr + ":" + $(this).text();
                });

                skuProperties.push(retStr);

                jamyInfo.skuProperties = skuProperties;
                callback(null, jamyInfo);
            }
        });
    }], function(error, jamyInfo) {
        callback(error, jamyInfo);
    });
};
