var mongoose = require('mongoose');
var async = require('async');

var People = require('../../dbmodels').People;
var BonusCode = require('../../dbmodels').BonusCode;
var Show = require('../../dbmodels').Show;
var Item = require('../../dbmodels').Item;

var RequestHelper = require('../../helpers/RequestHelper');
var ResponseHelper = require('../../helpers/ResponseHelper');
var MongoHelper = require('../../helpers/MongoHelper');
var SMSHelper = require('../../helpers/SMSHelper');
var BonusHelper = require('../../helpers/BonusHelper');

var errors = require('../../errors');

var admin = module.exports;

var _collectionToModel = {
    'items' : 'Item',
    'shows' : 'Show',
    'categories' : 'Category',
    'trades' : 'Trade'
};

admin.find = {
    'method' : 'get',
    // 'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        var model = req.queryString.model || _collectionToModel[req.queryString.collection];
        var Model = require('../../dbmodels/' + model);
        ServiceHelper.queryPaging(req, res, function(qsParam, callback) {
            // querier
            var criteria = MongoHelper.querySchema(Model, req.queryString);
            MongoHelper.queryPaging(Model.find(criteria).sort({
                'create' : -1
            }), Model.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
        }, function(models) {
            return {
                'models' : models
            };
        }, null);
    }
};

admin.queryItemByNickName = {
    'method' : 'post',
    'func' : function(req, res) {
        var param = req.body;
        var nickname = param.nickname;
        async.waterfall([function(callback){
            People.findOne({
                'nickname' : nickname
            }, function(err, people){
                callback(!people ? errors.PeopleNotExist : null, people);
            });
        }, function(people, callback){
            Show.find({
                'ownerRef' : people._id
            }).populate('itemRefs').exec(function(err, shows){
                callback(null, shows, people);
            });
        }, function(shows, people, callback){
            var items = shows.map(function(show){
                return show.itemRefs.filter(function(item){
                    return !item.delist;
                });
            }).filter(function(target){
                return target.length !== 0;
            });
            callback(!people ? errors.PeopleNotExist : null, items, people);
        }, function(items, people, callback){
            var total = 0;
            var noDraw = 0; 
            var results = [];
            for (var i = 0; i < items.length; i++) {
                for (var j = 0; j < items[i].length; j++) {
                    var item = items[i][j]; 
                    results.push({
                        name : item.name,
                        source : item.source,
                        promoterRef : people._id,
                        itemRef : item._id,
                        totalFee : item.promoPrice * 0.7,
                        quantity : 1,
                        promoPrice : item.promoPrice,
                        bonus : item.promoPrice * 0.7 * 0.02
                    });
                }

            };
            
            BonusHelper.aggregate(people, function(err, amountByStatus) {
                if (!err) {
                    noDraw = amountByStatus[BonusCode.STATUS_INIT];
                    total = amountByStatus[BonusCode.STATUS_INIT] +
                        amountByStatus[BonusCode.STATUS_REQUESTED] +
                        amountByStatus[BonusCode.STATUS_COMPLETE];
                }
                callback(null, results, people, total, noDraw);
            });
        }], function(err, results, people, total, noDraw){
            ResponseHelper.response(res, err, {
                'peopleRef' : people._id,
                'total' : total,
                'noDraw' : noDraw,
                'item' : results
            });
        });
    }
};

var _isFake = function(id){
    if(isNaN(id)) {
        return false
    } else {
        var n = parseInt(id);
        return n >=0 && n < 9000;
    }
}

admin.getPeople = {
    'method' : 'post',
    'func' : function(req, res){
        var param = req.body;
        var interval = {
            enable : param.interval.enable === true,
            upper : param.interval.upper,
            lower : param.interval.lower
        }
        async.waterfall([function(callback){
            People.find({
                '$where' : function(){
                    if(isNaN(this.userInfo.id)) {
                        return true
                    } else {
                        var n = parseInt(this.userInfo.id);
                        return !(n >=0 && n < 9000);
                    }
                },
                'create' : {
                    '$lt' : new Date(interval.upper),
                    '$gt' : new Date(interval.lower)
                }
            }, callback);
        }, function(peoples, callback){
            var target = [];
            async.eachSeries(peoples, function(people, callback){
                Show.count({
                    'ownerRef' : people._id
                }, function(err, count){
                    if (count > 0) {
                        target.push(people.nickname);
                    }
                    callback();
                })
            }, function(err){
                callback(null, target);
            })
        }], function(err, peoples){
            ResponseHelper.response(res, err, {
                'peoples' : peoples
            })
        })
    }
};


admin.getRealShow = {
    'method' : 'post',
    'func' : function(req, res){
        var param = req.body;
        var interval = {
            enable : param.interval.enable === true,
            upper : param.interval.upper,
            lower : param.interval.lower
        }
        async.waterfall([function(callback){
            Show.find({
            'create' : {
                '$lt' : new Date(interval.upper),
                '$gt' : new Date(interval.lower)
            }
        }, callback); 
        }, function(shows, callback){
            var targetOnwerRef = [];
            var target = [];
            async.eachSeries(shows, function(show, callback){
                if (targetOnwerRef.indexOf(show.ownerRef) === -1) {
                    People.findOne({
                        '_id' : show.ownerRef
                    }).exec(function(err, people){
                        if (people) {
                            var temp = parseInt(people.userInfo.id);
                            if(_isFake(people.userInfo.id)){
                            }else{
                                if (target.indexOf(people.nickname) === -1) {
                                    target.push(people.nickname);
                                }
                            }
                        }
                        targetOnwerRef.push(show.ownerRef);
                        callback();
                    })
                }
            }, function(err){
               callback(null, target); 
            })
        }], function(err, target){
            ResponseHelper.response(res, err, {
                'names' : target
            })
        })
    }
};

admin.sms = {
    'method' : 'post',
    'func' : [
        require('../middleware/injectCurrentUser'),
        require('../middleware/validateLoginAsAdmin'),
        require('../middleware/injectModelGenerator').generateInjectOneByObjectId(People, '_id', 'people'),
        function(req, res, next) {
            var mobile = req.injection.people ? req.injection.people.mobile : req.body.mobile;
            SMSHelper.sendTemplateSMS(mobile, req.body.datas, req.body.templateId, next);
        }
    ]
};
