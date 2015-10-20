var SharedObject = require('../../dbmodels').SharedObject;

var RequestHelper = require('../../helpers/RequestHelper');
var ResponseHelper = require('../../helpers/ResponseHelper');
var TraceHelper = require('../../helpers/TraceHelper');

var trace = module.exports;

trace.openShare = {
	method : 'post',
	func : function(req, res){
		var params = req.body;
		SharedObject.findOne({
			'_id' : RequestHelper.parseId(params._id)
		}).exec(function(err, sharedObject){
			TraceHelper.trace('behavior-openShare', req, {
				'_id' : sharedObject._id.toString()
			});
			ResponseHelper.response(res, err, {});
		})
	}
}

trace.downloadViaShare = {
	method : 'post',
	func : function(req, res){
		var params = req.body;
		SharedObject.findOne({
			'_id' : RequestHelper.parseId(params._id)
		}).exec(function(err, sharedObject){
			TraceHelper.trace('behavior-downloadViaShare', req, {
				'_id' : sharedObject._id.toString()
			});
			ResponseHelper.response(res, err, {});
		})
	}
}