var schedule = require('node-schedule'),
    async = require('async');

var Show = require('../../dbmodels').Show,
    SharedObject = require('../../dbmodels').SharedObject,
    SharedObjectCode = require('../../dbmodels').SharedObjectCode;

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
            // {'create' : {'$gte' : new Date(date.getTime() - 24 * ONE_HOUR)}},
            {'create' : {'$lt' : new Date(date.getTime() - 23 * ONE_HOUR)}}
        ]
    }, function(err, shows) {
        async.parallel(shows.map(function(show) {
            return function(callback) {
                SharedObject.count({
                    'initiatorRef' : show.ownerRef,
                    'type' : SharedObjectCode.TYPE_SHARE_SHOW,
                    'targetInfo.show.showRef' : show._id
                }, function(err, count) {
                    show.itemReductionEnabled = false;
                    if (count) {
                        show.numViewFirstDay = show.numView * 2;
                    } else {
                        show.numViewFirstDay = show.numView;
                    }
                    show.save(function(){});
                    callback();
                });
            };
        }), function() {});
    });
};

module.exports = function () {
    schedule.scheduleJob('0 * * * *', _run);
};
