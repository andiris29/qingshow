var mongoose = require('mongoose');
var async = require('async');

var Log = require('../../model/logs');

var RequestHelper = require('../helpers/RequestHelper');
var ResponseHelper = require('../helpers/ResponseHelper');

var ServerError = require('../server-error');
var ChannelPool = require('./ChannelPool');

var log = module.exports;

log.trace = {
    method : 'post',
    func : function(req, res) {
        var param;
        param = req.body;
        var clientIp = '';
        if (!req.header('X-Real-IP')) {
            clientIp = req.connection.remoteAddress;
        } else {
            clientIp = req.header('X-Real-IP');
        }

        var channel = '';
        var newlog = new Log({
            'ip' : clientIp,
            'behavior' : param.behavior,
            'deviceUid' : param.deviceUid,
            'osType' : param.osType,
            'osVersion' : param.osVersion
        });

        var saveCallback = function(err, log) {
            if (err) {
                ResponseHelper.response(res, err);
            } else if (!log) {
                ResponseHelper.response(res, ServerError.ServerError);
            } else {
                ResponseHelper.response(res, null, {
                    'log' : log
                });
            }
        };
        if (param.behavior === 'firstLaunch') {
            channel = ChannelPool.pool[clientIp];
            Log.findOne({
                'deviceUid' : param.deviceUid
            }, function(err, log) {
                if (err) {
                    ResponseHelper.response(res, err);
                } else if (log) {
                    ResponseHelper.response(res, ServerError.ServerError);
                } else {
                    if (channel === null || channel === undefined || channel.length === 0) {
                        ResponseHelper.response(res, ServerError.ServerError);
                    } else {
                        newlog.behaviorInfo = {
                            'firstLaunch' : {
                                'channel' : channel
                            }
                        };
                        
                        newlog.save(saveCallback);
                    }
                }
            });
        } else {
            newlog.save(saveCallback);
        }

    },

    
};
