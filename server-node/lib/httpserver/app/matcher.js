var mongoose = require('mongoose');
var async = require('async');
var path = require('path');
var _ = require('underscore');

// model
var Category = require('../../dbmodels').Category;
var Items = require('../../dbmodels').Item;
var Show = require('../../dbmodels').Show;
var ShowCode = require('../../dbmodels').ShowCode;
var People = require('../../dbmodels').People;

var loggers = require('../../runtime').loggers;
// util
var ResponseHelper = require('../../helpers/ResponseHelper');
var RequestHelper = require('../../helpers/RequestHelper');
var ServiceHelper = require('../../helpers/ServiceHelper');
var MongoHelper = require('../../helpers/MongoHelper.js');
var RelationshipHelper = require('../../helpers/RelationshipHelper');
var TraceHelper = require('../../helpers/TraceHelper');
var ContextHelper = require('../../helpers/ContextHelper');

var injectModelGenerator = require('../middleware/injectModelGenerator');
var errors = require('../../errors');

var matcher = module.exports;
 
var _isFake = function(people){
    if(isNaN(people.userInfo.id)) {
        return false;
    } else {
        var n = parseInt(people.userInfo.id);
        return (n >= 400 && n < 500) || (n > 600 && n < 700);
    }
};

var _shuffle = function (array) {
  var currentIndex = array.length, temporaryValue, randomIndex;
  while (0 !== currentIndex) {
    randomIndex = Math.floor(Math.random() * currentIndex);
    currentIndex -= 1;
    temporaryValue = array[currentIndex];
    array[currentIndex] = array[randomIndex];
    array[randomIndex] = temporaryValue;
  }

  return array;
};

matcher.queryCategories = {
    'method' : 'get',
    'func' : function(req, res) {
        Category.find({}).exec(function(err, categories) {
            ResponseHelper.response(res, err, {
                'categories' : categories
            },{
                'master' : global.qsMatcherConfig.common.master.categoryRef
            });
        });
    }
};

matcher.queryItems = {
    'method' : 'get',
    'func' : function(req, res) {
        var qsParam = req.queryString;
        async.waterfall([function(callback){
            Category.findOne({
                '_id' : RequestHelper.parseId(qsParam.categoryRef)
            }, callback);
        }], function(err, category){
            var criteria = category.matchInfo.excludeDelistBefore ? {
                'categoryRef' : category._id,
                '$or' : [{'delist' : {'$exists' : false}}, {'delist' : null}, {
                    'delist' : {'$gte' : category.matchInfo.excludeDelistBefore}
                }]
            } : {
                'categoryRef' : category._id,
                '$or' : [{'delist' : {'$exists' : false}}, {'delist' : null}]
            };

            var queryItems = req.session.queryItems || {};
            var pageNo;
            if (queryItems[category._id.toString()]) {
                pageNo = parseInt(queryItems[category._id.toString()]);
            }else {
                pageNo = parseInt(new Number(Math.random() * 10).toFixed(0));
            }
            pageNo = parseInt(qsParam.pageNo) + pageNo + 1;
            ServiceHelper.queryPaging(req, res, function(qsParam, callback) {
                MongoHelper.queryPaging(Items.find(criteria), Items.find(criteria), pageNo, 
                    qsParam.pageSize, function(err, models, count){
                        if ((!count || count < qsParam.pageSize) && pageNo != 0) {
                            pageNo = 0;
                            MongoHelper.queryPaging(Items.find(criteria), Items.find(criteria), pageNo, qsParam.pageSize - count, function(err, fillingModels, count){
                                callback(err, models ? models.concat(fillingModels) : fillingModels, qsParam.pageSize);
                            });
                            queryItems[category._id.toString()] = pageNo;
                            req.session.queryItems = queryItems;
                        }else {
                            queryItems[category._id.toString()] = pageNo;
                            req.session.queryItems = queryItems;
                            callback(err, models, count);
                        }
                    });
            }, function(items) {
                return {
                    'items' : _shuffle(items)
                }; 

            }, {
                'afterQuery' : function (qsParam, currentPageModels, numTotal, callback) {
                    if (qsParam.categoryRef === global.qsMatcherConfig.common.master.categoryRef) {
                        ContextHelper.appendMatchCompositionContext(currentPageModels, function(err, items){
                            callback(null, items);
                        });
                    }else {
                        callback(null, currentPageModels);
                    }
                }
            });
        });
    }
};

matcher.save = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        var featuredRank;
        async.waterfall([function(callback){
            People.findOne({
                '_id' : req.qsCurrentUserId
            }, callback);
        }, function(people, callback) {
            if (!req.body.itemRefs || !req.body.itemRefs.length) {
                ResponseHelper.response(res, errors.NotEnoughParam);
                return;
            }
            var itemRefs = RequestHelper.parseIds(req.body.itemRefs);
            var itemRects = req.body.itemRects;

            var coverUrl = global.qsConfig.show.coverForeground.template;
            coverUrl = coverUrl.replace(/\{0\}/g, _.random(1, global.qsConfig.show.coverForeground.max));

            var show = {};

            if (featuredRank) {
                show.featuredRank = featuredRank;
            }

            if (_isFake(people)) {
                show = {
                    'itemRefs' : itemRefs,
                    'itemRects' : itemRects,
                    'ownerRef' : req.qsCurrentUserId,
                    'coverForeground' : coverUrl,
                    'featuredRank' : 1
                }; 
            }else {
                show = {
                    'itemRefs' : itemRefs,
                    'itemRects' : itemRects,
                    'ownerRef' : req.qsCurrentUserId,
                    'coverForeground' : coverUrl
                };
            }
            show.featuredRank = people.talent ? ShowCode.FEATURED_RANK_TALENT : ShowCode.FEATURED_RANK_NORMAL;
            
            req.session.matcher = show;
            callback(null, show);
        }], function(err, show) {
            ResponseHelper.response(res, null, {});
        });
    }
};

matcher.updateCover = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        async.waterfall([function(callback){
            var show = req.session.matcher;
            if (show) {
                new Show(show).save(function(err, show){
                    callback(null, show);
                });
            }else{
                callback(errors.NotEnoughParam);
            }
        }, function(show, callback){
            RequestHelper.parseFile(req, global.qsConfig.uploads.show.cover.ftpPath, show._id.toString(), [
                {'suffix' : '_s', 'rate' : 0.5},
                {'suffix' : '_xs', 'rate' : 0.25}
                ], function (err, fields, file) {
                    if (err) {
                        callback(err);
                        return;
                    }
                    if (!file) {
                        callback(errors.NotEnoughParam);
                        return;
                    }
                    show.set('cover', global.qsConfig.uploads.show.cover.exposeToUrl + '/' + path.relative(global.qsConfig.uploads.show.cover.ftpPath, file.path));
                    var date = new Date();
                    date.setMinutes(date.getMinutes() - 10);
                    show.save(function(err, show) {
                        callback(null, show);
                    });
                });
        }], function(err, show){
            delete req.session.matcher;
            ResponseHelper.response(res, err, {
                'show' : show
            });
            // Log
            TraceHelper.trace('behavior-show-creation', req, {
                '_showId' : show._id.toString()
            });
        });
    }
};

matcher.hide = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        async.waterfall([
        function(callback) {
            Show.findOne({
                '_id' : RequestHelper.parseId(req.body._id),
                'ownerRef' : req.qsCurrentUserId
            }, function(err, show) {
                callback(err, show);
            });
        },
        function(show, callback) {
            if (show == null) {
                callback(errors.ShowNotExist);
                return;
            }
            show.hideAgainstOwner = true;
            show.save(function(err, show) {
                callback(err, show);
            });
        }],
        function(err, results) {
            if (!results) {
                ResponseHelper.response(res, errors.ShowNotExist);
            } else {
                ResponseHelper.response(res, err);
            }
        });
    }
};

matcher.remix = {
    method : 'get',
    func : function(req, res){
        var itemRef = req.queryString.itemRef,
        qsRemixConfig = global.qsRemixConfig;
        async.waterfall([function(callback){
            Items.findOne({
                '_id' : itemRef
            }, callback);
        }, function(item, callback){
            var element;
            for(var key in qsRemixConfig){
                var master = qsRemixConfig[key].master;
                if (_.isString(master.categoryRef) && master.categoryRef === item.categoryRef) {
                    element = qsRemixConfig[key];
                }else if (_.isArray(master.categoryRef) && _.contains(master.categoryRef, item.categoryRef)) {
                    element = qsRemixConfig[key];
                }else if (master.categoryRef === '*') {
                    element = qsRemixConfig[key];
                }
            }

            if (!element) {
                callback(errors.INVALID_OBJECT_ID);
                return;
            }
            callback(null, require('../../helpers/ConfigHelper').format(element), item);
        }, function(config, item, callback){
            var data = {},
            criteria = {};
            data.master = config.master;
            data.slaves = [];
            criteria = {
                $or: [{
                    shopRef: item.shopRef
                }, {
                    remix: true
                }]
            };
            var tasks = config.slaves.map(function(slave){
                return function(cb){
                    criteria.categoryRef = slave.categoryRef;
                    Items.find(criteria).exec(function(err, items){ 
                        if (items.length !== 0) {
                            var randomIndex = require('../../utils/RandomUtil').random(0, items.length);
                            slave.itemRef = items[randomIndex];
                            delete slave.categoryRef;
                            data.slaves.push(slave);   
                        }
                        cb(err);
                    });
                };
            });
            async.parallel(tasks, function(err){
                callback(err, data);
            });
        }], function(err, data){
            ResponseHelper.response(res, err, data);
        });
    }
};
