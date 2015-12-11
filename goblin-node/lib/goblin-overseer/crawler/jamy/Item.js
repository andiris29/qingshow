var winston = require('winston');
var async = require('async');
var iconv = require('iconv-lite');
var request = require('request');if (global.qsConfig && global.qsConfig.proxy) {request = request.defaults({'proxy' : global.qsConfig.proxy});}
var cheerio = require('cheerio');

var URLParser = require('../../../goblin-common/URLParser');

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

                if ($('.body-jamy-product').length === 0) {
                    callback(null, jamyInfo);
                    return;
                }

                var code = '';
                var idRegx = /product\/([A-Z0-9]+)/;
                var idComp = source.match(idRegx);

                if (idComp && idComp.length > 1) {
                    code = idComp[1];
                }
                var price = $('.prc-area-cny').text().split("￥");
                jamyInfo.promo_price = price[price.length - 1];
                jamyInfo.price = jamyInfo.promo_price;

                var skuProperties = [];
                var colorProperties = [];
                $('select').first().children('option').each(function(i, elm) {
                    colorProperties.push($(this).text());
                });


                var sizeProperties = [];
                $('select').last().children('option').each(function(i, elm) {
                    sizeProperties.push($(this).text());
                });

                var codes = [];
                var index = 1;
                for(var i = 0; i < colorProperties.length; i++) {
                    for (var j = 0; j < sizeProperties.length; j++) {
                        var offset = '0000' + index;
                        offset = offset.substr(-4);
                        codes.push(code + offset);
                    }
                }

                var skuTable = {};

                var jamyUrl = 'http://www.thejamy.com/common/discount/getPriceByProductItemCodes/' + codes.join('');
                request.get(jamyUrl, function(error, response, body) {
                    var datas = JSON.parse(body);

                    for(var i = 0; i < colorProperties.length; i++) {
                        for (var j = 0; j < sizeProperties.length; j++) {
                            var offset = '0000' + index;
                            offset = offset.substr(-4);
                            var newCode = code + offset;
                            skuTable[colorProperties[i] + ':' + sizeProperties[j]] = '1:' + datas['response'][newCode].prc.sum.cny + '';
                            if (i === 0 && j === 0) {
                                jamyInfo.promo_price = datas['response'][newCode].prc.sum.cny + '';
                            }
                        }
                    }

                    colorProperties = ['颜色'].concat(colorProperties);
                    sizeProperties = ['尺寸'].concat(sizeProperties);

                    skuProperties.push(colorProperties.join(':'));
                    skuProperties.push(sizeProperties.join(':'));

                    jamyInfo.skuProperties = skuProperties;
                    jamyInfo.skuTable = skuTable;
                    callback(null, jamyInfo);
                });
            }
        });
    }], function(error, jamyInfo) {
        callback(error, jamyInfo);
    });
};
