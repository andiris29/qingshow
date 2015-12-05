var mongoose = require('mongoose');
var async = require('async');
var path = require('path');
var _ = require('underscore');

// model
var Category = require('../../dbmodels').Category;
var Item = require('../../dbmodels').Item;
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
                'modelCategoryRef' : global.qsConfig.modelRemix.modelCategoryRef
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
                MongoHelper.queryPaging(Item.find(criteria), Item.find(criteria), pageNo, 
                    qsParam.pageSize, function(err, models, count){
                        if ((!count || count < qsParam.pageSize) && pageNo != 0) {
                            pageNo = 0;
                            MongoHelper.queryPaging(Item.find(criteria), Item.find(criteria), pageNo, qsParam.pageSize - count, function(err, fillingModels, count){
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
            });
        });
    }
};

matcher.save = {
    'method' : 'post',
    'func' : [
        require('../middleware/validateLogin'),
        function(req, res, next) {
            var date = new Date();
            date.setMinutes(date.getMinutes() - 60);
                
            Show.find({
                'ownerRef' : req.qsCurrentUserId,
                'create' : {'$gt' : date}
            }).count(function(err, count) {
                ResponseHelper.writeMetadata(res, {'limitCount' : global.qsConfig.matcher.limitCount});
                if (count < global.qsConfig.matcher.limitCount) {
                    next();
                } else {
                    next(errors.ERR_EXCEED_CREATE_SHOW_LIMIT);
                }
            });
        },
        function(req, res, next) {
            req.session.matcher = {
                'ownerRef' : req.qsCurrentUserId,
                'itemRefs' : RequestHelper.parseIds(req.body.itemRefs),
                'itemRects' : req.body.itemRects,
                'coverForeground' : global.qsConfig.show.coverForeground.template
                    .replace(/\{0\}/g, _.random(1, global.qsConfig.show.coverForeground.max))
            };
            next();
        }
    ]
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

var _injectRemixCategories = function(req, res, next) {
    async.parallel(req.injection.remixCategoryAliases.map(function(alias) {
        return function(callback) {
            Category.findOne({'alias' : alias}, callback);
        };
    }), function(err, results) {
        req.injection.remixCategories = results;
        next();
    });
};

var _parseRect = function(rect) {
    return rect.split(',').map(function(n) {
        return parseFloat(n);
    });
};

matcher.remixByModel = {
    'method' : 'get',
    'func' : [
        require('../middleware/injectModelGenerator').generateInjectOneByObjectId(Item, 'modelRef'),
        function(req, res, next) {
            req.injection.remixCategoryAliases = req.injection.modelRef.remixCategoryAliases.split(',');
            next();
        },
        _injectRemixCategories,
        function(req, res, next) {
            for(var composition in global.qsConfig.modelRemix) {
                var config = global.qsConfig.modelRemix[composition];
                if (_.keys(config).length === req.injection.remixCategories.length + 1) {
                    var data = {
                        'master' : {'rect' : _parseRect(config.master.rect)},
                        'slaves' : []
                    };
                    req.injection.remixCategories.forEach(function(category, index) {
                        data.slaves[index] = {
                            'categoryRef' : category._id.toString(),
                            'rect' : _parseRect(config['slave' + index].rect)
                        };
                    });
                    ResponseHelper.writeData(res, data);
                    break;
                }
            }
            next();
        }
    ]
};

matcher.remixByItem = {
    'method' : 'get',
    'func' : [
        require('../middleware/injectModelGenerator').generateInjectOneByObjectId(Item, 'itemRef'),
        function(req, res, next) {
            req.injection.itemRef.populate('categoryRef', function() {
                if (req.injection.itemRef.categoryRef) {
                    req.injection.remixCategoryAliases = req.injection.itemRef.categoryRef.remixCategoryAliases.split(',');
                    next();
                } else {
                    next(errors.ERR_INVALID_ITEM);
                }
            });
        },
        _injectRemixCategories,
        function(req, res, next) {
            async.parallel(req.injection.remixCategories.map(function(category) {
                return function(callback) {
                    Item.find({'categoryRef' : category._id}).count(function(err, count) {
                        Item.find({'categoryRef' : category._id}).populate('shopRef').skip(_.random(0, count - 1)).limit(1).exec(function(err, items) {
                            callback(err, items[0]);
                        });
                    });
                };
            }), function(err, results) {
                req.injection.remixItems = results;
                next();
            });
        },
        function(req, res, next) {
            for(var composition in global.qsConfig.itemRemix) {
                var config = global.qsConfig.itemRemix[composition];
                if (_.keys(config).length === req.injection.remixItems.length + 1) {
                    var data = {
                        'master' : {'rect' : _parseRect(config.master.rect)},
                        'slaves' : []
                    };
                    req.injection.remixItems.forEach(function(item, index) {
                        data.slaves[index] = {
                            'itemRef' : item,
                            'rect' : _parseRect(config['slave' + index].rect)
                        };
                    });
                    ResponseHelper.writeData(res, data);
                    break;
                }
            }
            next();
        }
    ]
};
