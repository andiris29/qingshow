
var async = require('async');
var Iconv = require('iconv').Iconv;
var request = require('request');
var taobaoWeb = module.exports;

taobaoWeb.item = {};

taobaoWeb.item.getWebSkus = function (item, callback) {
    async.waterfall([
        function (callback) {
            callback(null, item.source);
        },
        function (source, callback) {
            /*
             * http://detail.tmall.com/item.htm?spm=a1z10.4.w5003-9301197547.23.qjlW0g&id=40952601693&rn=2b5acab447292dedd6c7bc1d2e9a5387&abbucket=7&scene=taobao_shop
             * http://item.taobao.com/item.htm?spm=2013.1.w5734072-1564537832.10.Vg0Rmz&id=42888614090
             * */
            var tmallRegex = /http:\/\/\w*\.tmall\.com/;
            var taobaoRegex = /http:\/\/\w*\.taobao\.com/;
            var idRegex = /id=(\d*)/;
            var idComp = source.match(idRegex);
            var tbItemId = null;
            if (idComp && idComp.length > 1) {
                tbItemId = idComp[1];
            } else {
                // TODO handle parse id error
            }

            if (source.match(tmallRegex)) {
                _getTmallItemWebSkus(tbItemId, callback);
            } else if (source.match(taobaoRegex)) {
                _getTaobaoItemWebSkus(tbItemId, callback);
            } else {
                // TODO handle parse source error
            }
        }
    ], function (err, webSkus) {
        callback(null, webSkus);
    });
    //
};



var _getTaobaoItemWebSkus = function (tbItemId, callback) {
    request.get({
        'url' : 'http://detailskip.taobao.com/json/sib.htm?p=1&itemId=' + tbItemId,
        'headers' : {
            'referer' : 'http://item.taobao.com/item.htm?id=' + tbItemId,
            'accept-language' : 'en,en-US;q=0.8,zh-CN;q=0.6,zh;q=0.4'
        },
        'encoding' : 'binary'
    }, function (err, response, body) {
        if (err) {
            callback(err);
        } else {
            try {
                var g_config = {
                    'vdata' : {}
                };
                eval(new Iconv('gbk', 'utf-8').convert(new Buffer(body, 'binary')).toString());

//                var price, promoPrice;
                // TODO Parse g_config.PromoData to find the item.brandDiscountInfo.price
                // TODO Parse g_config.Price to find the price
                callback(null, null);
            } catch (e) {
                callback(e);
            }
        }
    });
};

var _getTmallItemWebSkus = function(tbItemId, callback) {
    request.get({
        'url' : 'http://mdskip.taobao.com/core/initItemDetail.htm?callback=setMdskip&itemId=' + tbItemId,
        'headers' : {
            'referer' : 'http://detail.tmall.com/item.htm?id=' + tbItemId,
            'accept-language' : 'en,en-US;q=0.8,zh-CN;q=0.6,zh;q=0.4'
        },
        'encoding' : 'binary'
    }, function (err, response, body) {
        if (err) {
            callback(err);
        } else {
            try {
                var isSetMdskipInvoke = false;
                var setMdskip = function(object) {
                    isSetMdskipInvoke = true;
                    var webSkus = [];
                    var priceInfo = object.defaultModel.itemPriceResultDO.priceInfo;
                    var stockInfo = object.defaultModel.inventoryDO.skuQuantity;
                    for (var sku_id in priceInfo) {
                        try {

                            var stock = stockInfo[sku_id].quantity;
                            var price = parseFloat(priceInfo[sku_id].promotionList[0].price);
                            var skuObj = {
                                'sku_id' : sku_id,
                                'promo_price' : price,
                                'stock' : stock
                            };
                            webSkus.push(skuObj);
                        } catch (e) {
                            console.log('Parse item :' + tbItemId + '  sku: ' + sku_id + ' error.');
                        }
                    }
                    callback(null, webSkus);
                };
                eval(new Iconv('gbk', 'utf-8').convert(new Buffer(body, 'binary')).toString());

                if (!isSetMdskipInvoke) {
                    console.log('Parse item :' + tbItemId + ' Error' );
                    //TODO handle error
                    callback(null, null);
                }
            } catch(e) {
                callback(e);
            }
        }
    });
};