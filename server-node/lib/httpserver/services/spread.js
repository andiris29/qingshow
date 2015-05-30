var mongoose = require('mongoose');
var async = require('async');

var Trace = require('../../model/traces');

var RequestHelper = require('../helpers/RequestHelper');
var ResponseHelper = require('../helpers/ResponseHelper');

var ServerError = require('../server-error');

var _channelStore = {};

var spread = module.exports;

spread.download = {
    'method' : 'get',
    'func' : function(req, res) {
        var ip = RequestHelper.getIp(req);
        _channelStore[ip] = req.queryString.channel;
        res.redirect('http://a.app.qq.com/o/simple.jsp?pkgname=com.focosee.qingshow');
    }
};

spread.firstLaunch = {
    'method' : 'post',
    'func' : function(req, res) {
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
        // firstLaunch
        Trace.findOne({
            'deviceUid' : param.deviceUid
        }, function(err, trace) {
            if (err) {
                ResponseHelper.response(res, err);
            } else if (trace) {
                ResponseHelper.response(res, ServerError.AlreadyLaunched);
            } else {
                newlog.behaviorInfo = {
                    'firstLaunch' : {
                        'channel' : _channelStore[ip]
                    }
                };
                newlog.save(saveCallback);
            }
        });
    }
};

