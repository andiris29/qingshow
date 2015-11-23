var SharedObject = require('../dbmodels').SharedObject;

var ShareHelper = module.exports;

ShareHelper.shareShowTitle = '时尚新玩法，快来为我的美搭点赞吧！';
ShareHelper.shareTradeTitle = '恭喜您获得活动最低折扣！你赚到啦！';
ShareHelper.shareBonusTitle = '原来玩搭配还能赚钱，我觉得我快要发财了...搭的越美，赚的越多';

ShareHelper.url = 'http://chingshow.com/app-web?_id={sharedObject.id}';
ShareHelper.icon = '';

ShareHelper.create = function(initiatorRef, type, title, targetInfo, callback){
	var sharedObject = new SharedObject();
	sharedObject.url = ShareHelper.url;
	sharedObject.icon = ShareHelper.icon;
	sharedObject.type = type;
	sharedObject.title = title;
	sharedObject.initiatorRef = initiatorRef;
	for(var key in targetInfo){
		sharedObject.targetInfo[key] = targetInfo[key];
	}
	sharedObject.save(function(err, sharedObject){
		callback(err, sharedObject);
	})
}