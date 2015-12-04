var async = require('async');
var winston = require('winston');

var RequestHelper = require('../../helpers/RequestHelper');
var ResponseHelper = require('../../helpers/ResponseHelper');
var errors = require('../../errors');
var VersionUtil = require('../../utils/VersionUtil');

var system = module.exports;
var logger = require('../../runtime').loggers.get('client');

var proDeployment = {
    appServiceRoot : 'http://chingshow.com/services',
    paymentServiceRoot : 'http://chingshow.com/payment',
    appWebRoot : 'http://chingshow.com/app-web'
};

var devDeployment = {
    appServiceRoot : 'http://dev.chingshow.com/services',
    paymentServiceRoot : 'http://dev.chingshow.com/payment',
    appWebRoot : 'http://dev.chingshow.com/app-web'
};

system.get = {
    'method' : 'get',
    'func' : function(req, res) {
        async.waterfall([
        function(callback) {
            var version = RequestHelper.getVersion(req);

            if (!version) {
                callback(errors.NotEnoughParam);
            } else {
                if (VersionUtil.gt(version, global.qsConfig.system.production.maxSupportedVersion)) {
                    callback(null, devDeployment);
                } else if (VersionUtil.gte(version, global.qsConfig.system.production.minSupportedVersion)) {
                    callback(null, proDeployment);
                } else {
                    callback(errors.UnsupportVersion);
                }
            }
        }], function(error, deployment) {
            ResponseHelper.response(res, error, {
                'deployment' : deployment
            });
        });
    }
};

system.getConfig = {
    'method' : 'get',
    'func' : [
        function(req, res, next) {
            ResponseHelper.writeData(res, {
                'config' : {
                    'event' : {
                        'image' : global.qsConfig.event.image
                    }
                }
            });
        }
    ]
};

system.log = {
    'method' : 'post',
    'func' : function(req, res) {
        var params = req.body;
        var level = params.level;
        var log = {
            'client' : params.client,
            'level' : params.level,
            'stack' : params.stack,
            'extra' : params.extra
        }

        var message = params.message;

        switch(level) {
            case 'info':
                logger.info(message, log);
                break;

            case 'error':
                logger.error(message, log);
                break;

            case 'warn':
                logger.warn(message, log);
                break;
        }

        ResponseHelper.response(res, null, {
        });
    }
}; 