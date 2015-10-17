var async = require('async');

var Show = require('../../dbmodels').Show;
var Trade = require('../../dbmodels').Trade;
var People = require('../../dbmodels').People;
var SharedObject = require('../../dbmodels').SharedObject;

var ShareHelper = require('../../helpers/ShareHelper');
var ResponseHelper = require('../../helpers/ResponseHelper');
var RequestHelper = require('../../helpers/RequestHelper');
var ServiceHelper = require('../../helpers/ServiceHelper');
var MongoHelper = require('../../helpers/MongoHelper');

var share = module.exports;

share.createShow = {
	method : 'post',
    permissionValidators : ['loginValidator'],
    func : function(req, res){
    	var params = req.body;
    	async.waterfall([function(callback){
    		Show.findOne({
    			'_id' : RequestHelper.parseId(params._id)
    		}, callback);
		}, function(show, callback){
			ShareHelper.create(req.qsCurrentUserId, 0, {
				'show' : {
					_id : show._id,
					cover : show.cover,
					coverForeground : show.coverForeground 
				}
			}, callback);
		}], function(err, shareObject){
			ResponseHelper.response(res, err, {
                'sharedObject' : shareObject 
            });
		});
    }
}


share.createTrade = {
	method : 'post',
	permissionValidators : ['loginValidator'],
	func : function(req, res){
		var params = req.body;
    	async.waterfall([function(callback){
    		Trade.findOne({
    			'_id' : RequestHelper.parseId(params._id)
    		}, callback);
		}, function(trade, callback){
			ShareHelper.create(req.qsCurrentUserId, 1, {
				'trade' : {
					_id : trade._id,
					totalFee : trade.totalFee,
					quantity : trade.quantity,
					itemSnapshot : {
						name : trade.itemSnapshot.name,
						promoPrice : trade.itemSnapshot.promoPrice
					}
				}
			}, callback);
		}], function(err, shareObject){
			ResponseHelper.response(res, err, {
                'sharedObject' : shareObject 
            });
		});
	}
}

share.createBonus = {
	method : 'post',
	permissionValidators : ['loginValidator'],
	func : function(req, res){
		var params = req.body;
    	async.waterfall([function(callback){
    		People.findOne({
    			'_id' : RequestHelper.parseId(params._id)
    		}, callback);
		}, function(people, callback){
			var total = 0;
			var withdrawTotal = 0;
			if (people.bonuses && people.bonuses.length > 0) {
				people.bonuses.forEach(function(bonus){
					if (bonus.status === 1) {
						withdrawTotal += bonus.money;
					}
					total += bonus.money;
				});
			}

			ShareHelper.create(req.qsCurrentUserId, 2, {
				'bonus' : {
					ownerRef : people._id,
					total : total,
					withdrawTotal : withdrawTotal
				}
			}, callback);
		}], function(err, shareObject){
			ResponseHelper.response(res, err, {
                'sharedObject' : shareObject 
            });
		});
	}
}

share.query = {
    'method' : 'get',
    'func' : function(req, res) {
        ServiceHelper.queryPaging(req, res,function(qsParam, callback){
            var criteria = {};
            if (qsParam._ids && qsParam._ids.length > 0) {
                criteria._id = {
                    '$in' : RequestHelper.parseIds(qsParam._ids)
                };
            }
            MongoHelper.queryPaging(SharedObject.find(criteria), SharedObject.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
        },function(sharedObjects){
            return {'sharedObjects': sharedObjects}
        })
    }
};

