var mongoose = require('mongoose');
var async = require('async');
var _ = require('underscore');

var People = require('../../dbmodels').People;
var Trade = require('../../dbmodels').Trade;
var jPushAudiences = require('../../dbmodels').JPushAudience;

var ResponseHelper = require('../../helpers/ResponseHelper');
var RequestHelper = require('../../helpers/RequestHelper');
var NotificationHelper = require('../../helpers/NotificationHelper');

var notify = module.exports;

notify.newRecommandations = {
    'method' : 'post',
    'func' : function(req, res) {
        var group = req.body.group;

        async.waterfall([
        function(callback) {
            People.find({
                'weight' : {'$ne' : null},
                'height' : {'$ne' : null}
            }).exec(callback);
        },
        function(peoples, callback) {
            var targets = _.filter(peoples, function(people) {
                var rate = people.weight / people.height;
                var type = null;
                if (rate < 0.275) {
                    type = 'A1';
                } else if (rate < 0.315) {
                    type = 'A2';
                } else if (rate < 0.405) {
                    type = 'A3';
                } else {
                    type = 'A4';
                }

                return type == group;
            });
            callback(null, targets);
        },
        function(targets, callback) {
            var ids = [];
            targets.forEach(function(target) {
                ids.push(target._id);
            });
            NotificationHelper.notify(ids, NotificationHelper.MessageNewRecommandations, {
                'command' : NotificationHelper.CommandNewRecommandations
            }, callback);
        }], function(err) {
            ResponseHelper.response(res, err, null);
        });
    }
};

