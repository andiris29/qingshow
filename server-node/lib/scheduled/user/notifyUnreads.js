var schedule = require('node-schedule');
var winston = require('winston');
var async = require('async');
    
var People = require('../../dbmodels').People;

var NotificationHelper = require('../../helpers/NotificationHelper');

var _next = function(today) {
	async.waterfall([function(callback){
		People.find({
			'unreadNotifications' : {$exists: true},
			'$where' : function(){
				return this.unreadNotifications.length > 0
			}
		}, callback);
	}, function(peoples, callback){
		var task = peoples.map(function(people){
			return function(cb){
				var unreadNotifications = people.unreadNotifications.sort(function(a, b){
					return a.create > b.create
				});
				for (var i = 0; i < unreadNotifications.length; i++) {
					var unread = unreadNotifications[i];
					var command = unread.extra.command;
					if(command === NotificationHelper.CommandNewBonus){
						NotificationHelper._push([people._id], NotificationHelper.MessageNewBonus.replace(/\{0\}/g, '一笔'), unread.extra, cb);
						break;
					}else if(command === NotificationHelper.CommandNewParticipantBonus){
						NotificationHelper._push([people._id], NotificationHelper.MessageNewBonus.replace(/\{0\}/g, '一笔'), unread.extra, cb);
						break;
					}
				}
				cb();
			};
		});
		async.parallel(task, callback);
	}], function(err, result) {
		winston.info('notifyUnreads complete');
	});
};

var _run = function() {
    var startDate = new Date();
    winston.info('notifyUnreads run at: ' + startDate);

    _next(startDate);
};

module.exports = function () {
    // var rule = new schedule.RecurrenceRule();
    // rule.hour = [new schedule.Range(10, 23)];
    // schedule.scheduleJob(rule, function () {
        // _run();
    // });
};
