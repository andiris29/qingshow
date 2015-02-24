var mongoose = require('mongoose');
var async = require('async'), _ = require('underscore');

var taobaoAPI = require('../taobao/taobaoAPI');
var taobaoWeb = require('../taobao/taobaoWeb');
var taobaoHelper = require('../taobao/taobaoHelper');

var TopShop = require('../../model/topShops');
var Item = require('../../model/items');

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
//                    var newNicks = nicks.filter(function (n) {
//                        return !shops.some(function (s) {
//                            return s.nick === n;
//                        });
//                    });
                    callback(null);
                });
            }
//            ,function (newNicks, callback) {
//                var tasks = [];
//                newNicks.forEach(function (nick) {
//                    tasks.push(function (callback) {
//                        // Query top
//                        taobaoAPI.shop.get({
//                            'nick' : nick,
//                            'fields' : 'sid,nick,title,desc,pic_path,shop_score'
//                        }, function (err, result) {
//                            if (err) {
//                                callback();
//                                return;
//                            }
//
//                            try {
//                                result.shop_get_response.shop.shop_score.delivery_score =
//                                    parseFloat(result.shop_get_response.shop.shop_score.delivery_score);
//                                result.shop_get_response.shop.shop_score.item_score =
//                                    parseFloat(result.shop_get_response.shop.shop_score.item_score);
//                                result.shop_get_response.shop.shop_score.service_score =
//                                    parseFloat(result.shop_get_response.shop.shop_score.service_score);
//                            } catch (e) { }
//
//                            var s = new TopShop(result.shop_get_response.shop);
//                            topShops.push(s);
//                            console.log(JSON.stringify(result.shop_get_response, null, 4));
//                            s.save(callback);
//                        });
//                    });
//                });
//                async.parallel(tasks, callback);
//            }
        ], function (err) {
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

var _crawlTaobaoInfo = function (item, callback) {
    async.waterfall([
        function (callback) {
            //Web Taobao Info
            taobaoWeb.item.getWebSkus(item, function (err, taobaoInfo) {
                item.taobaoInfo = taobaoInfo;
//                item.taobaoInfo = item.taobaoInfo || {};
//                item.taobaoInfo.web_skus = webSkus;
                callback();
            });
        }
    ], function (err) {
        if (err) {
            callback(err);
        } else {
            item.taobaoInfo = item.taobaoInfo || {};
            item.taobaoInfo.refreshTime = new Date();
            item.save(callback);
        }
    });
}

goblin.refreshItemTaobaoInfo = {
    'method' : 'get',
    'func' : function (req, res) {

        var qsParam = null;
        async.waterfall([
            function (callback) {
                //TODO check admin
                callback();
            },
            function (callback) {
                var itemId = null;
                try {
                    itemId = RequestHelper.parseId(req.queryString._id);
                } catch (e) {
                    callback(e);
                }
                callback(null, itemId);
            },
            function (itemId, callback) {
                Item.findOne({
                    "_id" : itemId
                }, callback);
            },
            function (item, callback) {
                _crawlTaobaoInfo(item, callback);
            },
        ], function (err, item) {
            ResponseHelper.response(res, err, {
                "item" : item
            });
        });
    },
    permissionValidators : ['loginValidator'] //TODO
};

goblin.batchRefreshItemTaobaoInfo = {
    'method' : 'get',
    'func' : function (req, res) {
        var qsParam = null;
        var items = null;
        async.waterfall([
            function (callback) {
                try {
                    qsParam = RequestHelper.parse(req.queryString);
                } catch (e) {
                    callback(e);
                    return;
                }
                callback();
            },
            function (callback) {
                //query items
                //TODO handle time
                var time = null;
                if (qsParam.startDate) {
                    time = RequestHelper.parseDate(qsParam.startDate);
                } else {
                    time = new Date();
                }
                var query = Item.find().or([{'taobaoInfo.refreshTime' : {'$exists' : false}}, {'taobaoInfo.refreshTime' : {'$lt' : time}}]);
                var queryCount = Item.find().or([{'taobaoInfo.refreshTime' : {'$exists' : false}}, {'taobaoInfo.refreshTime' : {'$lt' : time}}]);
                qsParam.pageNo = qsParam.pageNo || 1;
                qsParam.pageSize = qsParam.pageSize || 10;
                MongoHelper.queryPaging(query, queryCount, qsParam.pageNo, qsParam.pageSize, function(err, result) {
                    if (err) {
                        callback(err);
                    } else {
                        callback(null, result);
                    }
                });
            },
            function (result, callback) {
                items = result;
                var tasks = [];
                items.forEach(function (item) {
                    var task = function(callback) {
                        _crawlTaobaoInfo(item, callback);
                    };
                    tasks.push(task);
                });
                async.parallel(tasks, callback);
            }
        ], function (err) {
            ResponseHelper.response(res, err, {
                "items" : items
            });
        });
    },
    permissionValidators : ['adminValidator']
};