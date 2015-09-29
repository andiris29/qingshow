var async = require('async');

var RequestHelper = require('../helpers/RequestHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var ServerError = require('../server-error'); 
var VersionUtil = require('../../utils/VersionUtil');

var system = module.exports;

var productionDeployment = {
	appServiceRoot : 'http://chingshow.com/services',
	paymentServiceRoot : 'http://chinshow.com/payment',
	appWebRoot : 'http://chingshow.com/app-web' 
}

var researchDeployment = {
	appServiceRoot : 'http://dev.chingshow.com/services',
	paymentServiceRoot : 'http://dev.chingshow.com/payment',
	appWebRoot : 'http://chingshow.com/app-web' 
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