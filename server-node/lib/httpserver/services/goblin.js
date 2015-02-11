var mongoose = require('mongoose');
var async = require('async'), _ = require('underscore');
var taobao = require('../top/taobao');

var TopShop = require('../../model/topShop');

var MongoHelper = require('../helpers/MongoHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var RequestHelper = require('../helpers/RequestHelper');

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
                        taobao.shop.get({
                            'nick' : nick,
                            'fields' : 'sid,nick,title,desc,pic_path,shop_score'
                        }, function (err, result) {
                            if (err) {
                                console.log(err, null, 4);
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
//                            callback();
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
        // TODO
    }
};

goblin.queryItems = {
    'method' : 'get',
    'func' : function (req, res) {
        // TODO
    }
};

goblin.updateItemPrices = {
    'method' : 'post',
    'func' : function (req, res) {
        // TODO
    }
};
