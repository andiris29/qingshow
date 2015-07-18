var mongoose = require('mongoose');
var async = require('async');
var _ = require('underscore');

var People = require('../../model/peoples');

var ResponseHelper = require('../helpers/ResponseHelper');
var PushNotificationHelper = require('../helpers/PushNotificationHelper');

var notify = module.exports;

notify.newRecommandations = {
    'method' : 'post',
    'func' : function(req, res) {
        var group = req.body.group;
        
       
        async.waterfall([
        function(callback) {
            People.find({}).exec(callback);
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
            var registrationIDs = [];
            targets.forEach(function(people, index) {
                if(people.jPushInfo.registrationIDs && people.jPushInfo.registrationIDs.length > 0) {
                    registrationIDs = registrationIDs.concat(people.jPushInfo.registrationIDs);
                }
            });
            PushNotificationHelper.push(registrationIDs, "倾秀精选搭配上新，看看吧!", callback);
        }], function(err) {
            ResponseHelper.response(res, err, null);
        });
    }
};
