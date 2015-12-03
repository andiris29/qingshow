var SharedObject = require('../dbmodels').SharedObject,
    SharedObjectCode = require('../dbmodels').SharedObjectCode;

var ShareHelper = module.exports;

var _config = {};
_config[SharedObjectCode.TYPE_SHARE_SHOW] = {
    'title' : '时尚新玩法，快来为我的美搭点赞吧！',
    'icon' : ''
};
_config[SharedObjectCode.TYPE_SHARE_TRADE] = {
    'title' : '恭喜您获得活动最低折扣！你赚到啦！',
    'icon' : ''
};
_config[SharedObjectCode.TYPE_SHARE_BONUS] = {
    'title' : '原来玩搭配还能赚钱，我觉得我快要发财了...搭的越美，赚的越多',
    'icon' : ''
};

ShareHelper.create = function(initiatorRef, type, targetInfo, callback){
	var sharedObject = new SharedObject();
	sharedObject.type = type;
    sharedObject.icon = _config[type].icon;
    sharedObject.title = _config[type].title;
	sharedObject.initiatorRef = initiatorRef;
	for(var key in targetInfo){
		sharedObject.targetInfo[key] = targetInfo[key];
	}
	
	sharedObject.save(function(err, sharedObject) {
	    sharedObject.url = global.qsConfig.appWebRoot ＋ '?_id=' + sharedObject._id.toString();
        sharedObject.save(function(err, sharedObject) {
            callback(err, sharedObject);
        });
	});
};
