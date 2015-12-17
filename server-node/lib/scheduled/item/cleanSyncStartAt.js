var schedule = require('node-schedule'),
    async = require('async');

var Item = require('../../dbmodels').Item;

var logger = require('../../runtime').loggers.get('scheduled');

var ONE_DAY = 24 * 3600 * 1000;

var _run = function() {
    logger.info('Run cleanSyncStartAt');
    
    var date = new Date();
    date.setMinutes(date.getMinutes() - 5);
    
    Item.update(
        {'syncStartAt' : {'$lt' : date}},
        {'$set' : {'syncStartAt' : null}},
        {'multi' : true}, 
        function() {}
    );
};

module.exports = function () {
    schedule.scheduleJob('*/5 * * * *', _run);
};
