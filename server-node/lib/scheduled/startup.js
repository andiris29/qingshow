var schedule = require('node-schedule');
var winston = require('winston');
var goblinItem = require('./goblin/goblin-item');

module.exports = function () {
    _scheduleGoblinItem();
};

var _scheduleGoblinItem = function () {
    var rule = new schedule.RecurrenceRule();
    rule.hour = 1;
    rule.minute = 0;
    //Schedule goblin item
    schedule.scheduleJob(rule, function () {
        var startDate = new Date();
        winston.info('Goblin-tbitem daily begin at ' + startDate);
        goblinItem.start(startDate);
    });
    winston.info('Schedule goblin item success');
};