var mongoose = require('mongoose');
var async = require('async');

var Trace = require('../../models').Trace;
var People = require('../../models').People;
var jPushAudiences = require('../../models').JPushAudience;
var RequestHelper = require('../../helpers/RequestHelper');
var ResponseHelper = require('../../helpers/ResponseHelper');
var PushNotificationHelper = require('../../helpers/PushNotificationHelper');

var errors = require('../../errors');

var _channelStore = {};

var spread = module.exports;

spread.download = {
    'method' : 'get',
    'func' : function(req, res) {
        _openOrDownload(req, res, 'download');
    }
};

spread.open = {
    'method' : 'get',
    'func' : function(req, res) {
        _openOrDownload(req, res, 'open');
    }
};

var _openOrDownload = function(req, res, behavior) {
    var newlog = new Trace({
        'ip' : RequestHelper.getIp(req),
        'behavior' : 'open/download',
        'behaviorInfo' : {
            'entry' : req.entry,
            'initiatorRef' : RequestHelper.parseId(req.initiatorRef),
            'targetRef' : RequestHelper.parseId(req.targetRef)
        }
    });

    newlog.save(function(err, trace) {
        if (err) {
            ResponseHelper.response(res, err);
        } else if (!trace) {
            ResponseHelper.response(res, errors.genUnkownError());
        } else {
            ResponseHelper.response(res, null, {
                'trace' : trace
            });
        }
    });
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
                ResponseHelper.response(res, errors.genUnkownError());
            } else {
                ResponseHelper.response(res, null, {
                    'trace' : trace
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

