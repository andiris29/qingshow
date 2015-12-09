var SharedObject = require('../dbmodels').SharedObject,
    SharedObjectCode = require('../dbmodels').SharedObjectCode;

var ShareHelper = module.exports;

ShareHelper.create = function(initiatorRef, type, targetInfo, callback){
	var sharedObject = new SharedObject();
	sharedObject.type = type;
    sharedObject.icon = global.qsConfig.share[type].icon;
    sharedObject.title = global.qsConfig.share[type].title;
	sharedObject.initiatorRef = initiatorRef;
	for(var key in targetInfo){
		sharedObject.targetInfo[key] = targetInfo[key];
	}
	
	sharedObject.save(function(err, sharedObject) {
	    sharedObject.url = global.qsConfig.appWebRoot + '?_id=' + sharedObject._id.toString();
        sharedObject.save(function(err, sharedObject) {
            callback(err, sharedObject);
        });
	});
};
