var mongoose = require('mongoose');
var async = require('async'), _ = require('underscore');

var taobaoAPI = require('../top/taobaoAPI');
var taobaoWeb = require('../top/taobaoWeb');

var TopShop = require('../../model/topShops');
var Item = require('../../model/items')

var MongoHelper = require('../helpers/MongoHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var RequestHelper = require('../helpers/RequestHelper');
var ServiceHelper = require('../helpers/ServiceHelper');

var goblin = module.exports;


// E.g http://127.0.0.1:30001/services/goblin/queryTOPShops?nicks=粉白色小猪哒
goblin.queryTOPShops = {
    'method' : 'get',
    'func' : function (req, res) {
        var topShops;
        async.waterfall([
            function (callback) {
                callback(null, RequestHelper.parseArray(req.queryString.nicks));
            },
            function (nicks, callback) {
                TopShop.find({
                    'nick' : {
                        '$in' : nicks
                    }
                }).exec(function (err, shops) {
                    topShops = shops;
                    // Parse new nicks
                    var newNicks = nicks.filter(function (n) {
                        return !shops.some(function (s) {
                            return s.nick === n;
                        });
                    });
                    callback(null, newNicks);
                });
            },
            function (newNicks, callback) {
                var tasks = [];
                newNicks.forEach(function (nick) {
                    tasks.push(function (callback) {
                        // Query top
                        taobaoAPI.shop.get({
                            'nick' : nick,
                            'fields' : 'sid,nick,title,desc,pic_path,shop_score'
                        }, function (err, result) {
                            if (err) {
                                callback();
                                return;
                            }

                            try {
                                result.shop_get_response.shop.shop_score.delivery_score =
                                    parseFloat(result.shop_get_response.shop.shop_score.delivery_score);
                                result.shop_get_response.shop.shop_score.item_score =
                                    parseFloat(result.shop_get_response.shop.shop_score.item_score);
                                result.shop_get_response.shop.shop_score.service_score =
                                    parseFloat(result.shop_get_response.shop.shop_score.service_score);
                            } catch (e) { }

                            var s = new TopShop(result.shop_get_response.shop);
                            topShops.push(s);
                            console.log(JSON.stringify(result.shop_get_response, null, 4));
                            s.save(callback);
                        });
                    });
                });
                async.parallel(tasks, callback);
            }], function (err) {
            ResponseHelper.response(res, err, {
                'topShops' : topShops
            });
        });
    }
};

goblin.updateTOPShopHotSales = {
    'method' : 'post',
    'func' : function (req, res) {
        var qsParam = null;
        async.waterfall([
            function (callback) {
                try {
                    qsParam = RequestHelper.parse(req.body);
                } catch (e) {
                    callback(e);
                    return;
                }
                callback();
            },
            function (callback) {
                TopShop.findOne({
                    "nick" : qsParam.nick
                }, callback);
            },
            function (topShop, callback) {
                if (req.__context) {
                    topShop.__context = qsParam.__context;
                    topShop.save(callback);
                } else {
                    callback(null, topShop);
                }
            }
        ], function (err, topShop) {
            ResponseHelper.response(res, err, {
                "topShop" : topShop
            });
        });

    }
};

/*
goblin.queryItems = {
    'method' : 'get',
    'func' : function (req, res) {
        ServiceHelper.queryPaging(req, res, function (qsParam, callback) {
            MongoHelper.queryPaging(Item.find(), Item.find(), qsParam.pageNo, qsParam.pageSize, callback);
        }, function (models) {
            // responseDataBuilder
            return {
                'items' : models
            };
        });
    }
};


goblin.updateItemPrices = {
    'method' : 'post',
    'func' : function (req, res) {
        var qsParam = null;
        async.waterfall([
            function (callback) {
                try {
                    qsParam = RequestHelper.parse(req.body);
                } catch (e) {
                    callback(e);
                    return;
                }
                callback();
            },
            function (callback) {
                Item.findOne({
                    "_id" : qsParam._id
                }, callback);
            },
            function (item, callback) {
                if (qsParam.price) {
                    item.price = parseFloat(qsParam.price);
                }
                if (qsParam.brandDiscountInfo && qsParam.brandDiscountInfo.price) {
                    item.brandDiscountInfo = item.brandDiscountInfo || {};
                    item.brandDiscountInfo.price = parseFloat(qsParam.brandDiscountInfo.price);
                }
                callback(item);
            }
        ], function (err, item) {
            ResponseHelper.response(res, err, {
                "item" : item
            });
        });
    }
};
*/

goblin.refreshItemTaobaoInfo = {
    'method' : 'get',
    'func' : function (req, res) {

        var qsParam = null;
        async.waterfall([
            function (callback) {
                //TODO check admin

                //TODO catch exception of parseId
                var itemId = RequestHelper.parseId(req.queryString._id);

                callback(null, itemId);
            },
            function (itemId, callback) {
                Item.findOne({
                    "_id" : itemId
                }, callback);
            },
            function (item, callback) {
                taobaoWeb.item.getWebSkus(item, function (err, webSkus) {
                    item.taobaoInfo = item.taobaoInfo || {};
                    item.taobaoInfo.web_skus = webSkus;
                    callback(null, item);
                });
            },
            function (item, callback) {
                //TODO taobaoAPI
                callback(null, item);
            },
            function (item, callback) {
                //TODO save to db
                item.save(callback);
            }
        ], function (err, item) {
            ResponseHelper.response(res, err, {
                "item" : item
            });
        });
    }
};

goblin.batchRefreshItemTaobaoInfo = {
    'method' : 'post',
    'func' : function (req, res) {
        var qsParam = null;
        async.waterfall([
            function (callback) {

            },
            function (callback) {

            }
        ], function (err) {

        });
        return;
    }
};