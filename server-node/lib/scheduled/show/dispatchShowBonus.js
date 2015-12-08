var schedule = require('node-schedule'),
    async = require('async');

var BonusHelper = require('../../helpers/BonusHelper');

var Show = require('../../dbmodels').Show;

var logger = require('../../runtime').loggers.get('scheduled');

var ONE_DAY = 24 * 3600 * 1000;

var _run = function() {
    logger.info('Run dispatchShowBonus');
    
    var date = new Date();
    date.setHours(0);
    date.setMinutes(0);
    date.setSeconds(0);
    date.setMilliseconds(0);
    
    Show.aggregate([
        {'$match' : {
            '$and' : [
                {'numViewFirstDay' : {'$gt' : 0}},
                {'bonusRef' : null},
                {'create' : {'$gte' : new Date(date.getTime() - ONE_DAY)}},
                {'create' : {'$lt' : date}}
            ]
        }}, 
        {'$group' : {
            '_id' : '$ownerRef',
            'numViewFirstDay' : {'$sum' : '$numViewFirstDay'},
            'showRefs' : {'$push' : '$_id'}
        }}
    ], function(err, results) {
        results.forEach(function(result) {
            BonusHelper.createShowBonus(result._id, result.showRefs, result.numViewFirstDay, function(err, bonus) {
                Show.update(
                    {'_id' : {'$in' : result.showRefs}},
                    {'$set' : {'bonusRef' : bonus._id}},
                    {'multi' : true},
                    function(err) {}
                );
            });
        });
    });
};

module.exports = function () {
    schedule.scheduleJob('0 20 * * *', _run);
};
