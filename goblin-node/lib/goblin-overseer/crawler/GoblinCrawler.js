
var async = require('async');
var winston = require('winston');
var _ = require('underscore');

//Crawler
var TaobaoWebItem = require('./taobao/Item');
var HmWebItem = require('./hm/Item');
var JamyWebItem = require('./jamy/Item');

var ItemSourceType = require('../../goblin-common/ItemSourceType');
var ItemSourceUtil = require('../../goblin-common/ItemSourceUtil');

var GoblinError = require('../../goblin-common/GoblinError');
var GoblinCrawler = module.exports;

//Register crawler for different type
var typeToCrawler = {};
typeToCrawler[ItemSourceType.Taobao] = TaobaoWebItem;
typeToCrawler[ItemSourceType.Tmall] = TaobaoWebItem;
typeToCrawler[ItemSourceType.Hm] = HmWebItem;
typeToCrawler[ItemSourceType.Jamy] = JamyWebItem;



/**
 *
 * @param sourceUrl source url of item
 * @param callback function (err, webItem)
 *             webItem
 *               {
 *                   price : ,
 *                   promo_price : ,
 *                   skuProperties : ,
 *               }
 */
GoblinCrawler.crawl = function (sourceUrl, callback) {
    _crawlItemWebInfo(sourceUrl, callback);
};

var _getCrawlerWithSource = function (source) {
    if (!source) {
        return;
    }
    var sourceType = ItemSourceUtil.getType(source);
    if (sourceType === null) {
        return null;
    }
    return typeToCrawler[sourceType];
};

/**
 *
 * @param source
 * @param callback
 *            function (err, item, count, log)
 *                                          option.delist  boolean
 * @private
 */

var _crawlItemWebInfo = function (source, callback) {
    var crawler = _getCrawlerWithSource(source);
    if (!crawler) {
        callback(GoblinError.fromCode(GoblinError.NotSupportItemSource));
        return;
    }
    async.waterfall([
        function (callback) {
            crawler.getSkus(source, function (err, taobaoInfo) {
                if (err) {
                    callback(err);
                } else {
                    if (!taobaoInfo) {
                        callback(GoblinError.fromCode(GoblinError.InvalidItemSource));
                    } else {
                        callback(err, taobaoInfo);
                    }
                }
            });
        }
    ], function (err, taobaoInfo) {
        if (err) {
            callback(err);
        } else if (!taobaoInfo || !Object.keys(taobaoInfo).length) {
            callback(GoblinError.fromCode(GoblinError.Delist));
        } else {
            callback(null, taobaoInfo);
        }
    });
};
