var winston = require('winston');
var async = require('async');
var iconv = require('iconv-lite');
var request = require('request');
var cheerio = require('cheerio');

var URLParser = require('../../URLParser');

var HmWebItem = module.exports;

HmWebItem.getSkus = function(source, callback) {
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

                var hmInfo = {};

                var sizeLists = $('input', '.product-sizes');

                var ids = [];

                for (var i = 0; i < sizeLists.length; i++) {
                    ids.push(sizeLists[i].attribs['data-code']);
                };

                var url = 'http://www2.hm.com/zh_cn/getAvailability?variants=' + ids.join(',');

                request.get(url, function(error, response, body) {
                    var datas = null;
                    try {
                        datas = JSON.parse(body);
                    } catch (e) {}


                    if (!datas || datas.length === 0) {
                        callback(null, {});
                        return;
                    }

                    hmInfo.promo_price = $('.price-value', '.product-detail-meta').text().trim().replace(/¥/g,'');
                    if ($('.price-value-original','.product-detail-meta').length > 0) {
                        hmInfo.price = $('.price-value-original','.product-detail-meta').text().trim().replace(/¥/g,'');
                    } else {
                        hmInfo.price = hmInfo.promo_price;
                    }

                    hmInfo.price = hmInfo.price.replace(/,/g, '');
                    hmInfo.promo_price = hmInfo.promo_price.replace(/,/g, '');

                    var skuProperties = [];
                    var colorProperty = '颜色';
                    
                    var skuColor = $('input[name="product-color"]','.product-detail-meta');
                    if (skuColor.length > 1) {
                        for(var i = 0; i < skuColor.length; i++) {
                            colorProperty = colorProperty + ":" + skuColor[i].attribs.value;
                        }
                    } else if(skuColor.length == 1) {
                        colorProperty = colorProperty + ":" + skuColor.val();
                    }

                    skuProperties.push(colorProperty);

                    var sizeProperty = '尺寸';
                    var skuSize = $('.inputlist','.product-sizes').first().find('input');

                    if (skuSize.length > 1) {
                        for(var i = 0; i < skuSize.length; i++) {
                            sizeProperty = sizeProperty + ":" + skuSize[i].attribs.value;
                        }
                    } else if (skuSize.length == 1) {
                        sizeProperty = sizeProperty + ":" + skuSize.val();
                    }

                    skuProperties.push(sizeProperty);

                    hmInfo.skuProperties = skuProperties;

                    callback(null, hmInfo);

                });
            }
        });
    }], function(err, hmInfo) {
        callback(null, hmInfo);
    });
};
