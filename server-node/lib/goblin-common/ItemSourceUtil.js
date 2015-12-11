/**
 * Created by wxy325 on 15/9/8.
 */

var ItemSourceUtil = module.exports;

var URLParser = require('./URLParser');
var ItemSourceType = require('./ItemSourceType');

/**
 *
 * @param source
 * @param type combination type, e.g ItemSourceType.Taobao | ItemSourceTypeTmall
 * @returns {boolean}
 */
ItemSourceUtil.matchType = function (source, type) {
    for (var i = ItemSourceType.Min; i < ItemSourceType.Max; i = i << 1) {
        if (i & type) {
            var validator = ItemSourceUtil.getTypeValidator(i);
            if (validator && validator(source)) {
                return true;
            }
        }
    }
    return false;
};

/**
 * Return type of item source
 * @param {string} source
 * @returns {ItemSourceType} type of source, null if there is not match type
 */
ItemSourceUtil.getType = function (source) {
    if (!source) {
        return null;
    }
    for (var i =ItemSourceType.Min; i < ItemSourceType.Max; i = i << 1) {
        var validator = ItemSourceUtil.getTypeValidator(i);

        if (validator && validator(source)) {
            return i;
        }
    }
    return null;
};

/**
 *
 * @param type
 * @returns {*}
 */
ItemSourceUtil.getTypeValidator = function (type) {
    switch (type) {
        case ItemSourceType.Taobao:
            return URLParser.isFromTaobao ;
        case ItemSourceType.Tmall:
            return URLParser.isFromTmall;
        case ItemSourceType.Jamy:
            return URLParser.isFromJamy;
        case ItemSourceType.Hm:
            return URLParser.isFromHm;
        default :
            return null;
    }
};