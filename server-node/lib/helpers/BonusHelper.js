var async = require('async');

var Bonus = require('../dbmodels').Bonus,
    BonusCode = require('../dbmodels').BonusCode,
    People = require('../dbmodels').People,
    Show = require('../dbmodels').Show;

var NotificationHelper = require('../helpers/NotificationHelper');

var BonusHelper = module.exports;

BonusHelper.createTradeBonus = function(trade, cb) {
    async.waterfall([
    function(callback){
        // Find participants
        Show.find({
            'itemRefs' : {'$all' : [trade.itemRef]},
            'ownerRef' : {'$ne' : trade.promoterRef}
        }).sort({'numView' : -1}).limit(global.qsConfig.bonus.participants.count).exec(
            function(err, refs){callback(err, refs);}
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
            'trigger' : {
                'tradeRef' : trade._id
            },
            'participants' : participants
        });
        bonus.save(function(err) {callback(err, bonus);});
    },
    function(bonus, callback) {
        var message = NotificationHelper.MessageNewBonus.replace(/\{0\}/g, amount);
        NotificationHelper.notify([trade.promoterRef], message, {
            'command' : NotificationHelper.CommandNewBonus,
            '_id' : bonus._id.toString
        }, null);
        
        callback();
    }], function(err){
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
                    'trigger' : {
                        'tradeRef' : trade._id
                    }
                });
                bonus.save(function(err) {callback(err, bonus);});
            },
            function(bonus, callback) {
                var message = NotificationHelper.MessageNewBonus.replace(/\{0\}/g, amount);
                NotificationHelper.notify([peopleRef._id], message, {
                    'command' : NotificationHelper.CommandNewParticipantBonus,
                    '_id' : bonus._id.toString
                }, null);
                
                callback();
            }], function(err){
                callback(err);
            });
        };
    });
    async.parallel(tasks, callback);
};
