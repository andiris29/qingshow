var goblinItem =  module.exports;

var async = require('async');
var winston = require('winston');
var _ = require('underscore');

var Item = require('../../model/items');
// TODO Remove dependency on httpserver
var MongoHelper = require('../../httpserver/helpers/MongoHelper');
var taobaoMongoHelper = require('../../httpserver/taobao/taobaoMongoHelper');
var ServerError = require('../../httpserver/server-error');

goblinItem.start = function (startDate) {
    _next(startDate, 10);
};


var _next = function (time, retryTime) {
    async.waterfall([
        function (callback) {
            var criteria = {
                '$and': [{
                    '$or': [{
                        'taobaoInfo.refreshTime': {
                            '$exists': false
                        }
                    }, {
                        'taobaoInfo.refreshTime': {
                            '$lt': time
                        }
                    }]
                }, {
                    '$or': [{
                        'deactive': {
                            '$exists': false
                        }
                    }, {
                        'deactive': false
                    }]
                }]
            };

            var query = Item.find(criteria);
            var queryCount = Item.find(criteria);
            var pageNo = 1;
            var pageSize = 10;
            MongoHelper.queryPaging(query, queryCount, pageNo, pageSize, function (err, result, count) {
                winston.info('goblin item : remain item ' + count);
                if (err) {
                    callback(err);
                } else if (!count) {
                    callback(ServerError.PagingNotExist);
                } else {
                    callback(null, result);
                }
            });
        },
        function (result, callback) {
            items = result;
            var tasks = [];
            items.forEach(function (item) {
                var task = function (callback) {
                    taobaoMongoHelper.crawlItemTaobaoInfo(item, callback);
                };
                tasks.push(task);
            });
            async.parallel(tasks, callback);

        }
    ], function (err) {
        //TODO
        if (err === ServerError.PagingNotExist) {
            //finish
            winston.info('goblin item daily complete');
            return;
        }

        if (err) {
            winston.info('gobin item error:');
            winston.info(err);
            retryTime -= 1;
            if (retryTime > 0) {
                winston.info('remain retry : ' + retryTime);
                _.delay(function() {
                    _next(time, retryTime);
                }, 1000);
            } else {
                winston.info('Error: goblin item retry time is zero, stop.');
            }
        } else {
            _.delay(function () {
                _next(time, retryTime);
            }, 1000);
        }
    });
};
