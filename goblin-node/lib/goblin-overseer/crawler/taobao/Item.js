var winston = require('winston');
var async = require('async');
var Iconv = require('iconv-lite');
var request = require('request');if (global.qsConfig && global.qsConfig.proxy) {request = request.defaults({'proxy' : global.qsConfig.proxy});}
var cheerio = require('cheerio');
var URLParser = require('../../../goblin-common/URLParser');

var sizePrefixs = ["20509", "20518", "20549"];
var colorPrefixs = ["1627207"];

var _userAgent = 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36';

var TaobaoWebItem = module.exports;
TaobaoWebItem.getSkus = function (source, callback) {
    async.waterfall([
        function (callback) {
            callback(null, source);
        },
        function (source, callback) {
            var tbItemId = URLParser.getIidFromSource(source);

            if (URLParser.isFromTmall(source)) {
                _getTmallItemWebSkus(tbItemId, function (err, webSkus) {
                    if (err) {
                        callback(err);
                    } else {
                        _parseTmallWebPage(source, webSkus, callback);
                    }
                });
            } else if (URLParser.isFromTaobao(source)) {
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
        callback(err, taobaoInfo);
    });
    //
};

var _parseTaobaoWebPage = function (source, webSkus, callback) {
    var iid = URLParser.getIidFromSource(source);
    var url = 'http://item.taobao.com/item.htm?id=' + iid;
    request.get({
        'url' : url,
        'encoding' : 'binary'
    }, function (err, response, body) {
        if (err) {
            callback(err);
        } else {
            var b = Iconv.decode(new Buffer(body, 'binary'), 'gbk');

            var shopIdRegex = /shopId=(\d+)/;
            var matchResult = b.match(shopIdRegex);
            var shopId = null;
            if (matchResult && matchResult.length) {
                shopId = matchResult[1];
            }

            var $ = cheerio.load(b);
            var shopName = null;
            var shopTags = $('.tb-shop-info a');
            shopTags.each(function () {
                var t = cheerio(this).text();
                if (!shopName || !shopName.length) {
                    shopName = t;
                }
            });
            if (shopName) {
                shopName = shopName.trim();
            }
            var shopInfo = {
                shopId : shopId,
                shopName : shopName
            };

            var scriptTags = $('script');
            var hubConfigScript = null;
            scriptTags.each(function () {
                var scriptContent = cheerio(this).text();
                if (scriptContent.indexOf('Hub.config.set') !== -1) {
                    hubConfigScript = scriptContent;
                }
            });
            if (!hubConfigScript) {
                callback();
                return;
            }

            var Hub = {
                config : {}
            };
            try {
                Hub.config.set = function (key, obj) {
                    if (key !== 'sku') {
                        return;
                    }
                    winston.info(obj);
                };
            } catch (e) {
                winston.info(e);
            }
            var g_config = {};

            try {

                eval(hubConfigScript);
                var skuInfo = Hub.config.get('sku');
                var skuMap = skuInfo.valItemInfo.skuMap;
                var propertyMap = _parseTaobaoPropertyMap($);
                if (!propertyMap || !Object.keys(propertyMap).length) {
                    callback(null, {});
                } else {
                    var skus = _generateSkus(webSkus, skuMap, propertyMap);
                    var taobaoInfo = generateTaobaoInfoFromSkus(skus);
                    taobaoInfo.shopInfo = shopInfo;

                    callback(null, taobaoInfo);
                }

            } catch (e) {
                winston.info(e);
                callback(e);
            }
        }
    });
};
var generateTaobaoInfoFromSkus = function (skus){
    var taobaoInfo = {};


    if (skus.length) {
        var sku = skus[0];
        taobaoInfo.promo_price = sku.promo_price;
        taobaoInfo.price = sku.price;
    }
    var skuNames = skus.map(function (sku) {
        return sku.properties_name.split(';');
    });

    if (skuNames.length && skus.length) {
        var sku = skus[0];
        var skuIds = sku.properties.split(';').filter(function (s) {
            return s.length;
        });

        var skuProperties = [];
        for (var i = 0; i < skuNames[0].length; i++) {
            var skuId = skuIds[i];
            var retStr = "";
            if (colorPrefixs.some(function (p) {return skuId.indexOf(p) === 0;})) {
                retStr = "颜色";
            } else if (sizePrefixs.some(function (p) {return skuId.indexOf(p) === 0;})) {
                retStr = "尺码";
            }
            var names = [];
            skuNames.forEach(function (n) {
                if (names.indexOf(n[i]) === -1) {
                    names.push(n[i]);
                    retStr = retStr + ':' + n[i];
                }
            });
            skuProperties.push(retStr);
        }
        var skuTable = {};
        for (var j = 0; j < skus.length; j++) {
            var price = skus[j].promo_price;
            if (!price) {
                price = skus[j].price;
            }
            var stock = skus[j].stock;
            var key = skus[j].properties_name;
            key = key.replace(/;/g, ':');
            skuTable[key] = stock + ':' + price;
        }
        taobaoInfo.skuProperties = skuProperties;
        taobaoInfo.skuTable = skuTable;
    }
    return taobaoInfo;
};

// Tmall
var _parseTmallWebPage = function (source, webSkus, callback) {
    var iid = URLParser.getIidFromSource(source);
    var url = 'http://detail.tmall.com/item.htm?id=' + iid;
    var r = request.get({
        'url' : url,
        'encoding' : 'binary',
    }, function (err, response, body) {
        if (err) {
            callback(err);
        } else {
            var b = Iconv.decode(new Buffer(body, 'binary'), 'gbk');
            var shopIdRegex = /shopId=(\d+)/;
            var matchResult = b.match(shopIdRegex);
            var shopId = null;
            if (matchResult && matchResult.length) {
                shopId = matchResult[1];
            }

            var $ = cheerio.load(b);
            var shopTag = $('.slogo-shopname');
            var shopName = shopTag.text();

            var shopInfo = {
                shopId : shopId,
                shopName : shopName
            };

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
                if (!itemInfo) {
                    callback();
                    return;
                }
                var skuMap = itemInfo.skuMap;

                var propertyMap = _parseTmallPropertyMap($);
                if (!propertyMap || !Object.keys(propertyMap).length) {
                    //Item已下架
                    callback(null, {});
                } else {
                    var skus = _generateSkus(webSkus, skuMap, propertyMap);

                    var title = null;
                    $('meta').each(function () {
                        var i = cheerio(this);
                        if (i.attr('name') === 'keywords') {
                            title = i.attr('content');
                        }
                    });

                    var taobaoInfo = generateTaobaoInfoFromSkus(skus);
                    taobaoInfo.shopInfo = shopInfo;
                    callback(null, taobaoInfo);
                }
            };
            if(tshopSetupScript) {
                try {
                    eval(tshopSetupScript);
                } catch (e) {
                    callback(e);
                }
            } else {
                callback();
            }

        }
    });
    r.setMaxListeners(0);
};


var _getTmallItemWebSkus = function(tbItemId, callback) {
    request.get({
        'url' : 'http://mdskip.taobao.com/core/initItemDetail.htm?callback=setMdskip&itemId=' + tbItemId,
        'headers' : {
            'user-agent' : _userAgent,
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
                    if (!object.isSuccess) {
                        callback(null, []);
                        return;
                    }
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

                            if (priceInfo[key].wrtInfo) {
                                //双十一预售
                                price = parseFloat(priceInfo[key].wrtInfo.finalPayment) / 100.0 + parseFloat(priceInfo[key].wrtInfo.price) / 100.0;
                            } else if (priceInfo[key].promotionList && priceInfo[key].promotionList.length) {
                                // "type": "店铺vip",
                                // "promText": "登录后确认是否享有此优惠",
                                var promotion = priceInfo[key].promotionList[0];
                                if (promotion.promText && promotion.promText.indexOf("登录") !== -1) {
                                    price = parseFloat(priceInfo[key].price);
                                } else {
                                    price = parseFloat(priceInfo[key].promotionList[0].price);
                                }
                            } else if (priceInfo[key].suggestivePromotionList && priceInfo[key].suggestivePromotionList.length) {
                                var promotion = priceInfo[key].suggestivePromotionList[0];
                                price = parseFloat(promotion.price);
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
                            winston.info('Parse item :' + tbItemId + '  key: ' + key + ' error.');
                        }
                    }
                    callback(null, webSkus);
                };
                var b = Iconv.decode(new Buffer(body, 'binary'), 'gbk');
                try {
                    eval(b);
                } catch (err) {
                    winston.error('mdskip');
                    winston.error(b);
                    throw err;
                }

                if (!isSetMdskipInvoke) {
                    winston.info('Parse item :' + tbItemId + ' Error' );
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
                var bgRegex = /background:url\((.*)\)/;
                var matchResult = aStyle.match(bgRegex);
                if (matchResult.length > 1) {
                    propertyMap[dataValue].properties_thumbnail = matchResult[1];
                }
            }
        }
    });
    return propertyMap;
};


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
        };
        retSku.properties_name = _parsePropertiesName(retSku.properties, propertyMap);
        var thumbnail = _parsePropertiesThumbnail(retSku.properties, propertyMap);
        if (thumbnail) {
            retSku.properties_thumbnail = thumbnail;
        }
        return retSku;
    });
    var skus = skus.filter(function (s) { return s !== null; });
    return skus;
};


var _parsePropertiesName = function (propertiesStr, propertyMap) {
    var proNameArray = [];
    propertiesStr.split(';').forEach(function (prop) {
        if (prop && prop.length && propertyMap[prop]) {
            var value = propertyMap[prop].properties_name || propertyMap[prop];
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
        if (prop && prop.length && propertyMap[prop]) {
            var value = propertyMap[prop].properties_thumbnail;
            if (typeof value == 'string' && value.length) {
                propThumbnail = value;
            }
        }
    });
    return propThumbnail;
};


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
                var b = Iconv.decode(new Buffer(body, 'binary'), 'gbk');

                var g_config = {
                    vdata : {}
                };

                eval(b);

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
                        winston.info('Parse itemId: ' + tbItemId + ' sku: ' + key + ' Error');
                    }
                }
                callback(null, webSkus);
            } catch (e) {
                callback(e);
            }
        }
    });
};
