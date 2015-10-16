var mongoose = require('mongoose');
var async = require('async');

var People = require('../../dbmodels').People;
var Show = require('../../dbmodels').Show;
var Item = require('../../dbmodels').Item;

var RequestHelper = require('../../helpers/RequestHelper');
var ResponseHelper = require('../../helpers/ResponseHelper');
var MongoHelper = require('../../helpers/MongoHelper');

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
                callback(null, shows, people)
            });
        }, function(shows, people, callback){
            var items = shows.map(function(show){
                return show.itemRefs.filter(function(item){
                    return !item.delist
                })
            }).filter(function(target){
                return target.length !== 0;
            });
            callback(!people ? errors.PeopleNotExist : null, items, people);
        }], function(err, items, people){
            ResponseHelper.response(res, err, {
                'peopleRef' : people._id,
                'item' : items
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
                            if(temp > 0 && temp < 7580){
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
