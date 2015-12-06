var async = require('async');

var Bonus = require('../dbmodels').Bonus,
    BonusCode = require('../dbmodels').BonusCode,
    People = require('../dbmodels').People,
    Show = require('../dbmodels').Show;

var NotificationHelper = require('../helpers/NotificationHelper');

var errors = require('../errors');

var BonusHelper = module.exports;

BonusHelper.createTradeBonus = function(trade, cb) {
    async.waterfall([
    function(callback){
        // Find participants
        Show.find({
            'itemRefs' : {'$all' : [trade.itemRef]},
            'ownerRef' : {'$ne' : trade.promoterRef}
        }).sort({'numView' : -1}).limit(global.qsConfig.bonus.participants.count).exec(
            function(err, shows) {
                if (!err) {
                    callback(null, shows.map(function(show) {return show.ownerRef;}));
                } else {
                    callback(err);
                }
            }
        );
    }, function(peopleRefs, callback) {
        var amount = Math.round(Math.max(0.01, trade.totalFee * global.qsConfig.bonus.participants.rate) * 100) / 100;
        _createTradePaticipantBonus(trade, peopleRefs, amount, function(err){
            callback(null, peopleRefs);
        });
    }, function(peopleRefs, callback) {
        var amount = Math.round(Math.max(0.01, trade.totalFee * global.qsConfig.bonus.rate) * 100) / 100;
        _createTradeBonus(trade, amount, peopleRefs, callback);
    }], function(error, people) {
        cb(error, people);
    });
};

BonusHelper.createShowBonus = function(show, callback) {
    if (!show.numView) {
        callback();
        return;
    }
    async.waterfall([
        function(callback) {
            var bonus = new Bonus({
                'ownerRef' : show.ownerRef,
                'type' : BonusCode.TYPE_SHOW,
                'status' : BonusCode.STATUS_INIT,
                'amount' : show.numView * 0.01,
                'description' : '搭配收益',
                'icon' : 'http://trial01.focosee.com/img/misc/jiangbei.png',
                'trigger' : {
                    'showRef' : show._id
                }
            });
            bonus.save(function(err) {callback(err, bonus);});
        }
    ], function(err, bonus) {
        _notify(bonus);
        callback(err);
    });
};

var _notify = function(bonus) {
    NotificationHelper.notify(
        [bonus.ownerRef], 
        NotificationHelper.MessageNewBonus.replace(/\{0\}/g, bonus.amount + '元'), 
        {
            'command' : NotificationHelper.CommandNewBonus,
            '_id' : bonus._id.toString(),
            'type' : bonus.type
        },
    null);
};

BonusHelper.aggregate = function(ownerRef, callback) {
    Bonus.aggregate([
        {'$match' : {'ownerRef' : ownerRef}}, 
        {'$group' : {
            '_id' : '$status',
            'amount' : {'$sum' : '$amount'}
        }}
    ], function(err, results) {
        if (err) {
            callback(errors.genUnkownError(err));
        } else {
            var amountByStatus = {};
            amountByStatus[BonusCode.STATUS_INIT] = 0;
            amountByStatus[BonusCode.STATUS_REQUESTED] = 0;
            amountByStatus[BonusCode.STATUS_COMPLETE] = 0;
            results.forEach(function(result, index) {
                amountByStatus[result._id] = result.amount;
            });
            callback(null, amountByStatus);
        }
    });
};

var _createTradeBonus = function(trade, amount, participants, callback) {
    async.waterfall([
    function(callback) {
        var bonus = new Bonus({
            'ownerRef' : trade.promoterRef,
            'type' : BonusCode.TYPE_TRADE,
            'status' : BonusCode.STATUS_INIT,
            'amount' : amount,
            'description' : '来自' + trade.itemSnapshot.name + '的佣金',
            'icon' : trade.itemSnapshot.thumbnail,
            'trigger' : {
                'tradeRef' : trade._id
            },
            'participants' : participants
        });
        bonus.save(function(err) {callback(err, bonus);});
    }], function(err, bonus) {
        _notify(bonus);
        callback(err);
    });
};

var _createTradePaticipantBonus = function(trade, peopleRefs, amount, callback){
    var tasks = peopleRefs.map(function(peopleRef){
        return function(callback){
            async.waterfall([function(callback){
                var bonus = new Bonus({
                    'ownerRef' : peopleRef,
                    'type' : BonusCode.TYPE_TRADE_PARTICIPANT,
                    'status' : BonusCode.STATUS_INIT,
                    'amount' : amount,
                    'description' : '来自' + trade.itemSnapshot.name + '的佣金',
                    'icon' : trade.itemSnapshot.thumbnail,
                    'trigger' : {
                        'tradeRef' : trade._id
                    }
                });
                bonus.save(function(err) {callback(err, bonus);});
            }], function(err, bonus) {
                _notify(bonus);
                callback(err);
            });
        };
    });
    async.parallel(tasks, callback);
};
