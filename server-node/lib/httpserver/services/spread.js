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
        _channelStore[ip] = req.queryString['initiatorRef'];
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

