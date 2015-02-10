var mongoose = require('mongoose');
var async = require('async'), _ = require('underscore');
var taobao = require('../top/taobao');

var MongoHelper = require('../helpers/MongoHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var RequestHelper = require('../helpers/RequestHelper');

var goblin = module.exports;

// E.g http://127.0.0.1:30001/services/goblin/queryTOPShops?nicks=粉白色小猪哒
goblin.queryTOPShops = {
    'method' : 'get',
    'func' : function(req, res) {
        var topShops;
        async.waterfall([
        function(callback) {
            callback(null, RequestHelper.parseArray(req.queryString.nicks));
        },
        function(nicks, callback) {
            // TODO Query db.topShops
            topShops = [];
            // TODO Parse new nicks
            var newNicks = nicks;
            callback(null, newNicks);
        },
        function(newNicks, callback) {
            var tasks = [];
            newNicks.forEach(function(nick) {
                tasks.push(function(callback) {
                    // Query top
                    taobao.shop.get({
                        'nick' : nick,
                        'fields' : 'sid,nick,title,desc,pic_path,shop_score.item_score,shop_score.service_score,shop_score.delivery_score'
                    }, function(err, result) {
                        // TODO save to db.topShops, and push to local array topShops
                        console.log(JSON.stringify(result.shop_get_response, null, 4));
                        callback();
                    });
                });
            });
            async.parallel(tasks, callback);
        }], function(err) {
            ResponseHelper.response(res, err, {
                'topShops' : topShops
            });
        });
    }
};
