var mongoose = require('mongoose');
var async = require('async');
var path = require('path');
var _ = require('underscore');

// model
var Category = require('../../dbmodels').Category;
var Items = require('../../dbmodels').Item;
var Show = require('../../dbmodels').Show;
var People = require('../../dbmodels').People;

var loggers = require('../../runtime').loggers;
// util
var ResponseHelper = require('../../helpers/ResponseHelper');
var RequestHelper = require('../../helpers/RequestHelper');
var ServiceHelper = require('../../helpers/ServiceHelper');
var MongoHelper = require('../../helpers/MongoHelper.js');
var RelationshipHelper = require('../../helpers/RelationshipHelper');
var TraceHelper = require('../../helpers/TraceHelper');

var errors = require('../../errors');

var matcher = module.exports;

var _isFake = function(people){
    if(isNaN(people.userInfo.id)) {
        return false
    } else {
        var n = parseInt(people.userInfo.id);
        return (n >= 400 && n < 500) || (n > 600 && n < 700);
    }
}

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
}

matcher.queryCategories = {
    'method' : 'get',
    'func' : function(req, res) {
        Category.find({}).exec(function(err, categories) {
            ResponseHelper.response(res, err, {
                'categories' : categories
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
            }, {});
        })
    }
};

var _matchers = {};

matcher.save = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        var featuredRank;
        async.waterfall([function(callback){
            People.findOne({
                '_id' : req.qsCurrentUserId
            }, callback);
        }, function(people, callback){
            Show.find({
                'ownerRef' : people._id,
                'featuredRank' : {
                    $exists: true
                }
            }, function(err, shows){
                if (shows && shows.length > 0) {
                    featuredRank = shows[0].featuredRank;
                }
                callback(null, people, featuredRank);
            })
        }, function(people, featuredRank, callback) {
            if (!req.body.itemRefs || !req.body.itemRefs.length) {
                ResponseHelper.response(res, errors.NotEnoughParam);
                return;
            }
            var itemRefs = RequestHelper.parseIds(req.body.itemRefs);

            var coverUrl = global.qsConfig.show.coverForeground.template;
            coverUrl = coverUrl.replace(/\{0\}/g, _.random(1, global.qsConfig.show.coverForeground.max));

            if (_isFake(people)) {
                var show = new Show({
                    'itemRefs' : itemRefs, 
                    'ownerRef' : req.qsCurrentUserId,
                    'coverForeground' : coverUrl,
                    'featuredRank' : 1
                }); 
            }else {
                var show = new Show({
                    'itemRefs' : itemRefs, 
                    'ownerRef' : req.qsCurrentUserId,
                    'coverForeground' : coverUrl
                });
            }

            if (featuredRank) {
                show.featuredRank = featuredRank;
            }

            var uuid = require('node-uuid').v1();
            _matchers[uuid] = show;
            callback(null, uuid);
        }], function(err, uuid) {
            ResponseHelper.response(res, null, {
                'uuid' : uuid
            });
        })
    }
};

matcher.updateCover = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        RequestHelper.parseFile(req, global.qsConfig.uploads.show.cover.ftpPath, [
            {'suffix' : '_s', 'rate' : 0.5},
            {'suffix' : '_xs', 'rate' : 0.25}
        ], function (err, fields, file) {
            if (err) {
                ResponseHelper.response(res, err);
                return;
            }
            if (!fields.uuid || !fields.uuid.length) {
                ResponseHelper.response(res, errors.NotEnoughParam);
                return;
            }
            if (!file) {
                ResponseHelper.response(res, errors.NotEnoughParam);
                return;
            }
            var show = _matchers[fields.uuid];
            if (!show) {
                ResponseHelper.response(res, errors.NotEnoughParam);
                return;
            }
            show.set('cover', global.qsConfig.uploads.show.cover.exposeToUrl + '/' + path.relative(global.qsConfig.uploads.show.cover.ftpPath, file.path));
            
            var date = new Date();
            date.setMinutes(date.getMinutes() - 10);
            Show.findOne({
                'ownerRef' : show.ownerRef,
                'itemRefs' : show.itemRefs,
                'create' : {
                    '$gt' : date
                }
            }, function(err, duplicatedShow) {
                if (err || duplicatedShow) {
                    ResponseHelper.response(res, err, {
                        'show' : duplicatedShow
                    });
                } else {
                    show.save(function(err, show) {
                        ResponseHelper.response(res, err, {
                            'show' : show
                        });
                        // Log
                        TraceHelper.trace('behavior-show-creation', req, {
                            '_showId' : show._id.toString()
                        });
                    });
                }
            });
            
            delete _matchers[fields.uuid];
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
