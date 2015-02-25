
var async = require('async');
var Iconv = require('iconv').Iconv;
var request = require('request');
var cheerio = require('cheerio');

var taobaoHelper = require('./taobaoHelper');
var taobaoWeb = module.exports;


taobaoWeb.item = {};

taobaoWeb.item.getWebSkus = function (item, callback) {
    async.waterfall([
        function (callback) {
            callback(null, item.source);
        },
        function (source, callback) {
            var tbItemId = taobaoHelper.getIidFromSource(source);

            if (taobaoHelper.isFromTmall(source)) {
                _getTmallItemWebSkus(tbItemId, function (err, webSkus) {
                    if (err) {
                        callback(err);
                    } else {
                        _parseTmallWebPage(source, webSkus, callback);
                    }
                });
            } else if (taobaoHelper.isFromTaobao(source)) {
                _getTaobaoItemWebSkus(tbItemId, function (err, webSkus) {
                    if (err) {
                        callback(err);
                    } else {
                        _parseTaobaoWebPage(source, webSkus, callback);
                    }
                });
            } else {
                callback('parse source error');
                // TODO handle parse source error
            }
        }
    ], function (err, taobaoInfo) {
        callback(null, taobaoInfo);
    });
    //
};
var _parseTaobaoWebPage = function (source, webSkus, callback) {
    var iid = taobaoHelper.getIidFromSource(source);
    var url = 'http://item.taobao.com/item.htm?id=' + iid;
    request.get({
        'url' : url,
        'encoding' : 'binary'
    }, function (err, response, body) {
        if (err) {
            callback(err);
        } else {
            var b = new Iconv('gbk', 'utf-8').convert(new Buffer(body, 'binary')).toString();
            var $ = cheerio.load(b);
            var scriptTags = $('script');
            var hubConfigScript = null;
            scriptTags.each(function () {
                var scriptContent = cheerio(this).text();
                if (scriptContent.indexOf('Hub.config.set') !== -1) {
                    hubConfigScript = scriptContent;
                }
            });
            var Hub = {
                config : {}
            };
            try {
                Hub.config.set = function (key, obj) {
                    if (key !== 'sku') {
                        return;
                    }
                    console.log(obj);
                };
            } catch (e) {
                console.log(e);
            }

            try {
                eval(hubConfigScript);
                var skuInfo = Hub.config.get('sku');
                var skuMap = skuInfo.valItemInfo.skuMap;
                var propertyMap = _parseTaobaoPropertyMap($);
                var skus = _generateSkus(webSkus, skuMap, propertyMap);

                var top_title = $('.tb-main-title').attr('data-title');
                var nick = $('.tb-seller-name').attr('title');

                var taobaoInfo = {};
                taobaoInfo.skus = skus;
                taobaoInfo.top_num_iid = taobaoHelper.getIidFromSource(source);
                taobaoInfo.top_title = top_title;
                taobaoInfo.nick = nick;

                callback(null, taobaoInfo);
            } catch (e) {
                console.log(e);
                callback(e);
            }
        }
    });
};


// Tmall
var _parseTmallWebPage = function (source, webSkus, callback) {
    var iid = taobaoHelper.getIidFromSource(source);
    var url = 'http://detail.tmall.com/item.htm?id=' + iid;
    var r = request.get({
        'url' : url,
        'encoding' : 'binary',
    }, function (err, response, body) {
        if (err) {
            callback(err);
        } else {
            var b = new Iconv('gbk', 'utf-8').convert(new Buffer(body, 'binary')).toString();
            var $ = cheerio.load(b);

            var scriptTags = $('script');
            var tshopSetupScript = null;
            scriptTags.each(function () {
                var scriptContent = cheerio(this).text();
                if (scriptContent.indexOf('TShop.Setup') !== -1) {
                    tshopSetupScript = scriptContent;
                }
            });
            var TShop = {};
            var emptyFunc = function () {};
            TShop.poc = emptyFunc;
            TShop.setConfig = emptyFunc;
            TShop.Setup = function (obj) {
                var itemInfo = obj.valItemInfo;
                var skuMap = itemInfo.skuMap;

                var propertyMap = _parseTmallPropertyMap($);
                var skus = _generateSkus(webSkus, skuMap, propertyMap);

                var title = null;
                $('meta').each(function () {
                    var i = cheerio(this);
                    if (i.attr('name') === 'keywords') {
                        title = i.attr('content');
                    }
                });

                var nick = $('.slogo-shopname').text();
                var taobaoInfo = {};
                taobaoInfo.skus = skus;
                taobaoInfo.top_title = title;
                taobaoInfo.top_nick = nick;
                taobaoInfo.top_num_iid = taobaoHelper.getIidFromSource(source);
                callback(null, taobaoInfo);
            };
            try {
                eval(tshopSetupScript);
            } catch (e) {
                callback(e);
            }
        }
    });
    r.setMaxListeners(0);
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
//                    var stockInfo = object.defaultModel.inventoryDO.skuQuantity;
                    for (var key in priceInfo) {
                        if (key === 'dummy') {
                            continue;
                        }
                        try {
//                            var stock = stockInfo[key].quantity;
                            var price = null;
                            if (priceInfo[key].promotionList && priceInfo[key].promotionList.length && priceInfo[key].promotionList[0].price) {
                                price = parseFloat(priceInfo[key].promotionList[0].price);
                            } else {
                                price = parseFloat(priceInfo[key].price);
                            }

                            var skuObj = {
                                'key' : key,
                                'promo_price' : price
//                                'stock' : stock
                            };
                            webSkus.push(skuObj);
                        } catch (e) {
                            console.log('Parse item :' + tbItemId + '  key: ' + key + ' error.');
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
var _parseTaobaoPropertyMap = function ($) {
    var propertyMap = {};
    var liTags = $('.tb-skin li');
    liTags.each(function () {
        var this$ = cheerio(this);
        var dataValue = this$.attr('data-value');
        if (dataValue && dataValue.length) {
            var title = this$.attr('title');
            if (title && title.length) {
                propertyMap[dataValue] = title;
            } else {
                var sub$ = this$.find('span');
                propertyMap[dataValue] = sub$.text();
            }
        }
    });
    return propertyMap;
};

var _parseTmallPropertyMap = function ($) {
    var propertyMap = {};
    var liTags = $('.tb-sku li');
    liTags.each(function () {
        var this$ = cheerio(this);
        var dataValue = this$.attr('data-value');
        if (dataValue && dataValue.length) {
            propertyMap[dataValue] = {};
            var title = this$.attr('title');
            if (title && title.length) {
                propertyMap[dataValue].properties_name = title;
            } else {
                propertyMap[dataValue].properties_name = this$.text();
            }

            var aTag = this$.find('a');
            var aStyle = aTag.attr('style');
            if (aStyle && aStyle.length) {
                var bgRegex = /background:url\((.*)\)/
                var matchResult = aStyle.match(bgRegex);
                if (matchResult.length > 1) {
                    propertyMap[dataValue].properties_thumbnail = matchResult[1];
                }
            }
        }
    });
    return propertyMap;
}


var _generateSkus = function (webSkus, skuMap, propertyMap) {
    var skus = webSkus.map(function (webSku) {
        var targetKey = null;
        var targetValue = null;
        for (var mapKey in skuMap) {
            var mapValue = skuMap[mapKey];
            if (String(mapValue.skuId) === webSku.key || mapKey === webSku.key) {
                targetKey = mapKey;
                targetValue = mapValue;
                break;
            }
        }
        if (!targetKey || !targetKey) {
            return null;
        }

        var retSku = {
            sku_id : targetValue.skuId,
            properties : targetKey,
            properties_name : null,
            price : parseFloat(targetValue.price),
            promo_price : webSku.promo_price,
            stock : targetValue.stock
        }
        retSku.properties_name = _parsePropertiesName(retSku.properties, propertyMap);
        var thumbnail = _parsePropertiesThumbnail(retSku.properties, propertyMap);
        if (thumbnail) {
            retSku.properties_thumbnail = thumbnail;
        }
        return retSku;
    });
    var skus = skus.filter(function (s) { return s !== null; })
    return skus;
};


var _parsePropertiesName = function (propertiesStr, propertyMap) {
    var proNameArray = [];
    propertiesStr.split(';').forEach(function (prop) {
        if (prop && prop.length) {
            var value = propertyMap[prop].properties_name;
            if (typeof value == 'string' && value.length) {
                proNameArray.push(value);
            }
        }
    });
    return proNameArray.join(';');
};
var _parsePropertiesThumbnail = function (propertiesStr, propertyMap) {
    var propThumbnail = null;
    propertiesStr.split(';').forEach(function (prop) {
        if (prop && prop.length) {
            var value = propertyMap[prop].properties_thumbnail;
            if (typeof value == 'string' && value.length) {
                propThumbnail = value;
            }
        }
    });
    return propThumbnail;
}


var _getTaobaoItemWebSkus = function (tbItemId, callback) {
    request.get({
        'url' : 'http://detailskip.taobao.com/json/sib.htm?p=1&rcid=16&chnl&price=7000&shopId&vd=1&skil=false&pf=1&al=false&ap=1&ss=0&free=0&st=1&ct=1&prior=1&ref&itemId=' + tbItemId,
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
                    vdata : {}
                };
                eval(new Iconv('gbk', 'utf-8').convert(new Buffer(body, 'binary')).toString());

                var priceBeforeDiscount = parseFloat(g_config.Price);
//                var stockInfo = g_config.DynamicStock.sku;
                var priceInfo = g_config.PromoData;
                var webSkus = [];
                var key;
                for (key in priceInfo) {
                    if (key === 'dummy') {
                        continue;
                    }
                    try {
                        var price = null;
                        if (priceInfo[key] && priceInfo[key].length && priceInfo[key][0] && priceInfo[key][0].price) {
                            price = parseFloat(priceInfo[key][0].price);
                        } else {
                            price = priceBeforeDiscount;
                        }
//                        var stock = parseInt(stockInfo[key].stock);
                        var sku = {
                            'key' : key,
                            'promo_price' : price
//                            'stock' : stock
                        };
                        webSkus.push(sku);
                    } catch (e) {
                        console.log('Parse itemId: ' + tbItemId + ' sku: ' + key + ' Error');
                    }
                }
                callback(null, webSkus);
            } catch (e) {
                callback(e);
            }
        }
    });
};
