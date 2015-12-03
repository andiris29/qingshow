var schedule = require('node-schedule'),
    async = require('async');

var BonusHelper = require('../../helpers/BonusHelper');

var Show = require('../../dbmodels').Show;

var logger = require('../../runtime').loggers.get('scheduled');

var ONE_HOUR = 1 * 3600 * 1000;

var _run = function() {
    logger.info('Run switchReductionEnabled');
    
    var date = new Date();
    date.setMinutes(0);
    date.setSeconds(0);
    date.setMilliseconds(0);
    
    Show.find({
        '$and' : [
            {'itemReductionEnabled' : true},
            {'create' : {'$gte' : new Date(date.getTime() - 24 * ONE_HOUR)}},
            {'create' : {'$lt' : new Date(date.getTime() - 23 * ONE_HOUR)}}
        ]
    }, function(err, shows) {
        shows.forEach(function(show) {
            show.itemReductionEnabled = false;
            show.save(function(){});
            
            BonusHelper.createShowBonus(show, function(){});
        });
    });
};

module.exports = function () {
    schedule.scheduleJob('0 * * * *', _run);
};
