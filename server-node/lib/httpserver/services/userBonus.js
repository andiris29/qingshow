var mongoose = require('mongoose');
var async = require('async');
var _ = require('underscore');

var People = require('../../model/peoples');
var Trades = require('../../model/trades');
var jPushAudiences = require('../../model/jPushAudiences');

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
            for (var i = 0; i < people.bonuses; i++) {
                if (people.bonuses[i].status === 0) {
                    people.bonuses[i].status = 1;
                    people.bonuses[i].alipayId = req.body.alipayId;
                }
            }
            people.save(function(error, people) {
                if (error) {
                    callback(error);
                } else if (people) {
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
                '_id' : RequestHelper.parseId(req.body._id)
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
            var count = parseInt(req.body.count);
            var sum = req.body.sum;
            for (var i = 0; i < people.bonuses; i++) {
                if (count > 0) {
                    if (people.bonuses[i].status === 1) {
                        people.bonuses[i].status = 2;
                        count--;
                    }
                } else {
                    break;
                }
            }
            people.save(function(error, people) {
                if (error) {
                    callback(error);
                } else if (people) {
                    callback(ServerError.ServerError);
                } else {
                    callback(error, people, sum);
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
        var mapReduce = {
            map : function() {
                var sum = 0,
                    count = 0;
                (this.bonuses || []).forEach(function(bonus) {
                    if (bonus.status === 1) {
                        sum += bonus.money;
                        count++;
                    }
                });
                if (sum > 0) {
                    emit(this._id, {
                        'id' : this.userInfo.id,
                        'nickname' : this.nickname,
                        'sum' : sum,
                        'count' : count
                    });
                }
            }, 
            reduce : function(_id, array) {
                return array[0];
            },
            query : {
                bonuses : {
                    '$ne' : null
                }
            },
            out : {
                inline : 1
            }
        };
        Trades.mapReduce(mapReduce, function(error, data) {
            if (error) {
                ResponseHelper.response(res, error);
                return;
            }

            var rows = _.map(data.results, function(n) {
                return {
                    _id : n._id,
                    id : n.value.id,
                    nickname : n.value.nickname,
                    sum : n.value.sum,
                    count : n.value.count
                };
            });
            ResponseHelper.response(res, null, {
                rows : rows
            });
        });
    }
};
