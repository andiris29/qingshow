var mongoose = require('mongoose');
var async = require('async');
var _ = require('underscore');

var People = require('../../model/peoples');
var Trade = require('../../model/trades');
var jPushAudiences = require('../../model/jPushAudiences');

var ResponseHelper = require('../helpers/ResponseHelper');
var RequestHelper = require('../helpers/RequestHelper');
var PushNotificationHelper = require('../helpers/PushNotificationHelper');

var notify = module.exports;

notify.newRecommandations = {
    'method' : 'post',
    'func' : function(req, res) {
        var group = req.body.group;

        async.waterfall([
        function(callback) {
            People.find({}).exec(callback);
        },
        function(peoples, callback) {
            var targets = _.filter(peoples, function(people) {
                var rate = people.weight / people.height;
                var type = null;
                if (rate < 0.275) {
                    type = 'A1';
                } else if (rate < 0.315) {
                    type = 'A2';
                } else if (rate < 0.405) {
                    type = 'A3';
                } else {
                    type = 'A4';
                }

                return type == group;
            });

            var ids = [];
            targets.forEach(function(target) {
                ids.push(target._id);
            });
            callback(null, ids);
        },
        function(ids, callback) {
            jPushAudiences.find({
                peopleRef : {
                    '$in' : ids
                }
            }).exec(function(err, infos) {
                callback(err, infos);
            });
        },
        function(targets, callback) {
            var registrationIDs = [];
            targets.forEach(function(target) {
                registrationIDs.push(target.registrationId);
            });
            PushNotificationHelper.push(registrationIDs, PushNotificationHelper.MessageNewRecommandations, {
                'command' : PushNotificationHelper.CommandNewRecommandations
            }, callback);
        }], function(err) {
            ResponseHelper.response(res, err, null);
        });
    }
};

notify.itemPriceChanged = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        async.waterfall([
        function(callback) {
            Trade.find({
                'itemRef' : RequestHelper.parseId(req.body._id),
                'status' : 0
            }).exec(callback);
        },
        function(trades, callback) {
            var tasks = trades.map(function(trade) {
                return function(cb) {
                    async.waterfall([
                    function(cb2) {
                        jPushAudiences.find({
                            peopleRef : trade.ownerRef
                        }).exec(function(err, infos) {
                            cb2(err, infos);
                        });
                    },
                    function(infos, cb2) {
                        var registrationIDs = [];
                        infos.forEach(function(target) {
                            registrationIDs.push(target.registrationId);
                        });
                        PushNotificationHelper.push(registrationIDs, PushNotificationHelper.MessageItemPriceChanged, {
                            'command' : PushNotificationHelper.CommandItemPriceChanged,
                            '_id' : req.body._id,
                            '_tradeId' : trade._id.toString(),
                            'actualPrice' : req.body.actualPrice
                        }, cb2);
                    }], cb);
                };
            });
            async.parallel(tasks, function(err) {
                callback();
            });
        }], function(err) {
            ResponseHelper.response(res, err, null);
        });
    }
};
