var SharedObject = require('../dbmodels').SharedObject;

var ShareHelper = module.exports;

ShareHelper.create = function(initiatorRef, type, targetInfo, callback){
	var sharedObject = new SharedObject();
	sharedObject.type = type;
	sharedObject.initiatorRef = initiatorRef;
	for(var key in targetInfo){
		sharedObject.targetInfo[key] = targetInfo[key];
	}
	sharedObject.save(function(err, sharedObject){
		callback(err, sharedObject);
	})
}