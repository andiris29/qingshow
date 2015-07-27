var mongoose = require('mongoose');
var async = require('async');

var Trace = require('../../model/traces');
var People = require('../../model/peoples');
var jPushAudiences = require('../../model/jPushAudiences');
var RequestHelper = require('../helpers/RequestHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var PushNotificationHelper = require('../helpers/PushNotificationHelper');

var ServerError = require('../server-error');

var _channelStore = {};

var spread = module.exports;

spread.download = {
    'method' : 'get',
    'func' : function(req, res) {
        var ip = RequestHelper.getIp(req);
        _channelStore[ip] = req.queryString['questSharing.initiatorRef'];
        res.redirect('http://a.app.qq.com/o/simple.jsp?pkgname=com.focosee.qingshow');
    }
};

spread.firstLaunch = {
    'method' : 'post',
    'func' : function(req, res) {
        var MAX_PROGRESS = global.qsConfig.quest.maxProgress;
        var param = req.body;
        var ip = RequestHelper.getIp(req);
        var newlog = new Trace({
            'ip' : ip,
            'behavior' : 'firstLaunch',
            'deviceUid' : param.deviceUid,
            'osType' : param.osType,
            'osVersion' : param.osVersion
        });

        var saveCallback = function(err, trace) {
            if (err) {
                ResponseHelper.response(res, err);
            } else if (!trace) {
                ResponseHelper.response(res, ServerError.ServerError);
            } else {
                ResponseHelper.response(res, null, {
                    'trace' : trace
                });

                var initiatorRef = trace.behaviorInfo.firstLaunch.initiatorRef;
                People.findOne({
                    '_id' : initiatorRef
                }).exec(function(err, people) {
                    if (people) {
                        if (people.questSharing && people.questSharing.status == 0) {
                            people.questSharing.progress = people.questSharing.progress + 1;
                            var message = "";
                            var extras = {};
                            if (people.questSharing.progress >= MAX_PROGRESS) {
                                people.questSharing.status = 1;
                                message = PushNotificationHelper.MessageQuestSharingObjectiveComplete;
                                extras = {
                                    'command' : PushNotificationHelper.CommandQuestSharingObjectiveComplete
                                };
                            } else {
                                message = PushNotificationHelper.MessageQuestSharingProgress;
                                var objective = MAX_PROGRESS - people.questSharing.progress;
                                message = message.replace("{0}", objective);
                                extras = {
                                    'command' : PushNotificationHelper.CommandQuestSharingProgress
                                };
                            }
                            jPushAudiences.find({
                                'peopleRef' : people._id
                            }).exec(function(err, registrationIDs) {
                                if (err) {
                                    return;
                                }
                                var targets = [];
                                registrationIDs.forEach(function(id) {
                                    targets.push(id.registrationId);
                                });
                                PushNotificationHelper.push(targets, message, extras, null);
                            });
                            people.save();
                        } else if(people.questSharing == null || people.questSharing.status == null) {
                            people.questSharing = {
                                status : 0,
                                progress : 1, 
                                reward : {}
                            };
                            var alertMessage = PushNotificationHelper.MessageQuestSharingProgress;
                            var target = MAX_PROGRESS - people.questSharing.progress;
                            alertMessage = alertMessage.replace("{0}", target);
                            jPushAudiences.find({
                                'peopleRef' : people._id
                            }).exec(function(err, registrationIDs) {
                                if (err) {
                                    return;
                                }
                                var targets = [];
                                registrationIDs.forEach(function(id) {
                                    targets.push(id.registrationId);
                                });
                                PushNotificationHelper.push(targets, alertMessage, {
                                    'command' : PushNotificationHelper.CommandQuestSharingProgress
                                }, null);
                            });
                            people.save();
                        }
                    }
                });
            }
        };
        //newlog.save(saveCallback);
        // firstLaunch
        Trace.findOne({
            'deviceUid' : param.deviceUid
        }, function(err, trace) {
            if (err) {
                ResponseHelper.response(res, err);
            } else if (trace) {
                ResponseHelper.response(res);
            } else {
                newlog.behaviorInfo = {
                    firstLaunch : {
                        initiatorRef : RequestHelper.parseId(_channelStore[ip])
                    }
                };
                newlog.save(saveCallback);
            }
        });
    }
};

spread.questSharingComplete = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        var id = req.body.id;
        var receiverUuid = req.body.receiverUuid;
        People.findOne({
            '_id' : req.qsCurrentUserId
        }).exec(function(err, people) {
            if (err) {
                ResponseHelper.response(res, err);
            } else if (!people) {
                ResponseHelper.response(res, ServerError.PeopleNotExist);
            } else {
                people.questSharing.reward = {
                    'id' : id,
                    'receiverUuid' : receiverUuid
                };
                people.save(function(err, people) {
                    ResponseHelper.response(res, err, {
                        'people' : people
                    });
                });
            }
        });
    }
};
