var mongoose = require('mongoose');
var async = require('async');
var _ = require('underscore');

var People = require('../../model/peoples');
var jPushAudiences = require('../../model/jPushAudiences');
var Item = require('../../model/items');

var RequestHelper = require('../helpers/RequestHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var PushNotificationHelper = require('../helpers/PushNotificationHelper');
var BonusHelper = require('../helpers/BonusHelper');
var ServerError = require('../server-error');

var userBonus = module.exports;

userBonus.forge = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        var param = req.body;
        async.waterfall([
        function(callback) {
            Item.findOne({
                '_id' : RequestHelper.parseId(param.itemRef)
            }).exec(function(err, item) {
                if (err) {
                    callback(err);
                } else if (!item) {
                    callback(ServerError.ItemNotExist);
                } else {
                    callback(null, item);
                }
            });
        },
        function(item, callback) {
            BonusHelper.updateBonuse(RequestHelper.parseId(param.promoterRef), {
                forgerRef : req.qsCurrentUserId
            },  (item.promoPrice * 0.9), item.name, callback);
        }], function(error, people) {
            ResponseHelper.response(res, error, {
                'people' : people
            });
        });
    }
};

userBonus.withDraw = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        async.waterfall([
        function(callback) {
            People.findOne({
                '_id' : req.qsCurrentUserId
            }).exec(function(error, people) {
                if (error) {
                    callback(error);
                } else if (!people) {
                    callback(ServerError.PeopleNotExist);
                } else {
                    callback(null, people);
                }
            });
        },
        function(people, callback) {
            people.bonuses = people.bonuses || [];
            people.bonuseWithdrawRequested = true;
            for (var i = 0; i < people.bonuses; i++) {
                people.bonuses[i].status = 1;
            }
            people.save(function(error, people) {
                if (error) {
                    callback(error);
                } else if (!people) {
                    callback(ServerError.ServerError);
                } else {
                    callback(error, people);
                }
            });
        }], function(error, people) {
            ResponseHelper.response(res, error);
        });
    }
};

userBonus.withdrawComplete = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        async.waterfall([
        function(callback) {
            People.findOne({
                '_id' : req.qsCurrentUserId
            }).exec(function(error, people) {
                if (error) {
                    callback(error);
                } else if (!people) {
                    callback(ServerError.PeopleNotExist);
                } else {
                    callback(null, people);
                }
            });
        },
        function(people, callback) {
            people.bonuses = people.bonuses || [];
            people.bonuseWithdrawRequested = false;
            var total = 0;
            for (var i = 0; i < people.bonuses; i++) {
                people.bonuses[i].status = 2;
                total += people.bonuses[i].money;
            }
            people.save(function(error, people) {
                if (error) {
                    callback(error);
                } else if (!people) {
                    callback(ServerError.ServerError);
                } else {
                    callback(error, people, total);
                }
            });
        },
        function(people, total, callback) {
            jPushAudiences.find({
                'peopleRef' : people._id
            }).exec(function(err, infos) {
                if (infos.length > 0) {
                    var targets = [];
                    infos.forEach(function(element) {
                        if (element.registrationId && element.registrationId.length > 0) {
                            targets.push(element.registrationId);
                        }
                    });
                    var message = PushNotificationHelper.MessageBonusWithdrawComplete.replace(/\{0\}/g, total);
                    PushNotificationHelper.push(targets, message, {
                        'command' : PushNotificationHelper.CommandBonusWithdrawComplete
                    }, null);
                }
            });
            callback(null, people);
        }], function(error, people) {
            ResponseHelper.response(res, error);
        });
    }
};

userBonus.queryWithdrawRequested = {
    'method' : 'get',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        People.find({
            bonuseWithdrawRequested : true
        }).exec(function(error, peoples) {
            ResponseHelper.response(res, error, {
                'peoples' : peoples
            });
        });
    }
};
