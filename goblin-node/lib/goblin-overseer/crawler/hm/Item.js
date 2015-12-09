var winston = require('winston');
var async = require('async');
var iconv = require('iconv-lite');
var request = require('request');if (global.qsConfig && global.qsConfig.proxy) {request = request.defaults({'proxy' : global.qsConfig.proxy});}
var cheerio = require('cheerio');

var URLParser = require('../../../goblin-common/URLParser');

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

                    var scripts = $('script', '.product');
                    eval(scripts[0].children[0].data);

                    var colorPriceTable = productArticleDetails;
                    hmInfo.promo_price = $('.price-value', '.product-detail-meta').text().trim().replace(/¥/g,'');
                    if ($('.price-value-original','.product-detail-meta').length > 0) {
                        hmInfo.price = $('.price-value-original','.product-detail-meta').text().trim().replace(/¥/g,'');
                    } else {
                        hmInfo.price = hmInfo.promo_price;
                    }

                    hmInfo.price = hmInfo.price.replace(/,/g, '');
                    hmInfo.promo_price = hmInfo.promo_price.replace(/,/g, '');

                    var skuProperties = [];
                    var skuTable = {};
                    var colorProperties = [];
                    var sizeProperties = [];
                    colorProperties.push('颜色');
                    sizeProperties.push('尺寸');

                    var sizeLists = $('input','.product-sizes');
                    
                    // color code sample = 0261676010
                    // size code sample  = 0261676038020
                    datas.forEach(function(size) {
                        var color = size.substr(0, 10);
                        var colorName = colorPriceTable[color].name;
                        var price = colorPriceTable[color].priceSaleValue;
                        if (!price) {
                            price = colorPriceTable[color].priceValue;
                        }
                        if (colorProperties.indexOf(colorName) < 0) {
                            colorProperties.push(colorName);
                        }

                        // get size name 
                        var sizeName = '';
                        for(var i = 0; i < sizeLists.length; i++) {
                            if (sizeLists[i].attribs['data-code'] == size) {
                                sizeName = sizeLists[i].attribs.value;
                                break;
                            }
                        }

                        if (sizeProperties.indexOf(sizeName) < 0) {
                            sizeProperties.push(sizeName);
                        }

                        var key = colorName + ':' + sizeName;
                        skuTable[key] = '1:' + price;
                    });

                    skuProperties.push(colorProperties.join(':'));
                    skuProperties.push(sizeProperties.join(':'));

                    hmInfo.skuProperties = skuProperties;
                    hmInfo.skuTable = skuTable;

                    callback(null, hmInfo);

                });
            }
        });
    }], function(err, hmInfo) {
        callback(null, hmInfo);
    });
};
