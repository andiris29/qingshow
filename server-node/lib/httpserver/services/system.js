var async = require('async');
var winston = require('winston');

var RequestHelper = require('../helpers/RequestHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var ServerError = require('../server-error'); 
var VersionUtil = require('../../utils/VersionUtil');

var system = module.exports;
var clientLogger = winston.loggers.get('client');

var productionDeployment = {
	appServiceRoot : 'http://chingshow.com/services',
	paymentServiceRoot : 'http://chingshow.com/payment',
	appWebRoot : 'http://chingshow.com/app-web' 
}

var researchDeployment = {
	appServiceRoot : 'http://139.196.32.82/services',
	paymentServiceRoot : 'http://139.196.32.82/payment',
	appWebRoot : 'http://139.196.32.82/app-web' 
}

system.get = {
	'method' : 'get',
	'func' : function(req, res){
		async.waterfall([function(callback){
			var deployment = {};
			var versionLimit = global.qsConfig.system.production.maxSupportedVersion;
			var version = req.queryString.version;
			if (!version) {
				callback(ServerError.NotEnoughParam);
			}else {
				if (VersionUtil.greater(version, versionLimit)) {
					deployment = researchDeployment;
				}else {
					deployment = productionDeployment;
				}
				callback(null, deployment);
			}
		}],function(error, deployment){
		    ResponseHelper.response(res, error, {
		    	'deployment' : deployment
		    });
		});
	}
};


system.log = {
	'method' : 'post',
	'func' : function(req, res){
		var params = req.body;
		var level = params.level;
		var log = {
			'client' : params.client,
			'level' : params.level,
			'stack' : params.stack,
			'extra' : params.extra
		}

		var message =  params.message;

		switch(level){
			case 'info':
				clientLogger.info(message, log);
			break;
			
			case 'error':
				clientLogger.error(message, log);
			break;

			case 'warn':
				clientLogger.warn(message, log);
			break;
		}

		ResponseHelper.response(res, null, {
		});
	}
};