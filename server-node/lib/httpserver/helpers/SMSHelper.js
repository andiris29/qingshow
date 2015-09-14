var crypto = require('crypto');
var request = require('request');
var moment = require('moment');

var ServerError = require('../server-error');

var YTX_SID = 'aaf98f894fa5766f014fa72f897102e6';
var YTX_TOKEN = '999249ff15ad4222aa268c7374430107';
var APP_ID = '8a48b5514fb1a66a014fb545362305a1';

var TEMPLATE_ID = '36286';

var _verifications = {};

var SMSHelper = module.exports;

_decryptMD5 = function (string){
    return crypto.createHash('md5')
    .update(string)
    .digest('hex')
    .toUpperCase();
}

_base64Encoder = function(string){
	return new Buffer(string).toString('base64');
}

SMSHelper.sendTemplateSMS = function (to, datas, templateId, callback){
	var timestamp = moment(new Date()).format('YYYYMMDDHHmmss');

	var sig = _decryptMD5(YTX_SID + YTX_TOKEN + timestamp);
	var authorization = _base64Encoder(YTX_SID + ':' + timestamp);

	var url = 'https://app.cloopen.com:8883/2013-12-26/Accounts/'+YTX_SID+'/SMS/TemplateSMS?sig=' + sig;

	 request.post({
        url: url,
        headers: {
            'Accept' : 'application/json',
            'Content-Type' : 'application/json;charset=utf-8',
            'Authorization' : authorization
        },
        body: JSON.stringify({
                to : to,
                appId : APP_ID,
                templateId : templateId || TEMPLATE_ID,
                datas : datas
            })
    },function(err, res, body){
    	var result = JSON.parse(body);
    	if (err || result.statusCode !== '000000') {
    		callback(ServerError.ServerError);
    	}else {
    		callback(null, body);    			
    	}
    	_cleanExpiredCode();
    });
}

SMSHelper.createVerificationCode = function (to, callback){
	var config = global.qsConfig;
	if (_verifications.to && 
		new Date() - _verifications.to.create < config.verification.retry) {
		callback(ServerError.ServerError);
	}else {
		var code = new Number(Math.random() * Math.pow(10,6)).toFixed(0);
		_verifications[to] = {
			'verificationCode' : code,
			'create' : new Date()
		}
		callback(null, _verifications[to].verificationCode);
	}
}

SMSHelper.checkVerificationCode = function (to, code, callback){
	var config = global.qsConfig;		
	var verification = _verifications[to];
	if (verification && code == verification.verificationCode && 
		new Date() - verification.create < config.verification.expire) {
		callback(null, true);
	}else {
		callback(ServerError.SMSValidationFail, false);
	}
	_cleanExpiredCode();
}

var _cleanExpiredCode = function (){
	var config = global.qsConfig;	
	for(var to in _verifications){
		if (new Date() - _verifications[to].create > config.verification.expire) {
			_verifications[to] = {};
		}
	}
}


