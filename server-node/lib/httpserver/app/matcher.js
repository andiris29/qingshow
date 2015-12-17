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
var ItemSyncHelper = require('../../helpers/ItemSyncHelper');

var injectModelGenerator = require('../middleware/injectModelGenerator');

var errors = require('../../errors');

var QUERY_ITEMS_NUM_TOTAL = 1000 * 1000;

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
    'func' : [
        require('../middleware/injectModelGenerator').generateInjectOneByObjectId(Category, 'categoryRef'),
        function(req, res, next) {
            var category = req.injection.categoryRef;
            var pageInfo = RequestHelper.parsePageInfo(req.queryString),
                pageNo = pageInfo.pageNo,
                pageSize = pageInfo.pageSize;
            var criteria = {
                'categoryRef' : category._id,
                '$or' : [
                    {'delist' : null}
                ]
            };
            if (category.matchInfo.excludeDelistBefore) {
                criteria['$or'].push(
                    {'delist' : {'$gte' : category.matchInfo.excludeDelistBefore}}
                );
            }
            req.session.matcherQueryItems = req.session.matcherQueryItems || {};
            
            var closure = {};
            async.series([
                function(callback) {
                    // Count
                    Item.find(criteria).count(function(err, count) {
                        var initialSkip = req.session.matcherQueryItems[category._id.toString()];
                        if (initialSkip === undefined) {
                            initialSkip = req.session.matcherQueryItems[category._id.toString()] = _.random(0, count);
                        }
                        
                        closure.count = count;
                        closure.skip = (initialSkip + pageNo * pageSize) % count;
                        callback(err);
                    });
                },
                function(callback) {
                    // Query from skip
                    Item.find(criteria).skip(closure.skip).limit(pageSize).exec(function(err, items) {
                        closure.items = items;
                        callback(err);
                    });
                },
                function(callback) {
                    // Query from 0
                    if (closure.items.length < pageSize) {
                        Item.find(criteria).limit(pageSize - closure.items.length).exec(function(err, items) {
                            closure.items = closure.items.concat(items);
                            callback(err);
                        });
                    } else {
                        callback();
                    }
                }
            ], function(err) {
                if (err) {
                    next(errors.genUnkownError(err));
                } else {
                    ResponseHelper.write(res, 
                        {'numTotal' : QUERY_ITEMS_NUM_TOTAL, 'numPages' : Math.floor(QUERY_ITEMS_NUM_TOTAL / pageSize)},
                        {'items' : _shuffle(closure.items)});
                    next();
                }
            });
        }
    ]
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
                ResponseHelper.writeMetadata(res, {
                    'limitMessage' : global.qsConfig.matcher.limitMessage.replace(/\{0\}/g, global.qsConfig.matcher.limitCount)
                });
                if (count < global.qsConfig.matcher.limitCount) {
                    next();
                } else {
                    next(errors.ERR_EXCEED_CREATE_SHOW_LIMIT);
                }
            });
        },
        function(req, res, next) {
            var duplicated = true;
            if (req.session.matcherSave) {
                if (req.session.matcherSave.itemRefs.length !== req.body.itemRefs.length) {
                    duplicated = false;
                } else {
                    for (var i = 0; i < req.session.matcherSave.itemRefs.length; i++) {
                        if (req.session.matcherSave.itemRefs[i].toString() !== req.body.itemRefs[i]) {
                            duplicated = false;
                            break;
                        }
                    }
                }
            } else {
                duplicated = false;
            }
            
            if (duplicated) {
                next(errors.genUnkownError('Duplicated save request.'));
            } else {
                req.session.matcherSave = {
                    'ownerRef' : req.qsCurrentUserId,
                    'itemRefs' : RequestHelper.parseIds(req.body.itemRefs),
                    'itemRects' : req.body.itemRects,
                    'coverForeground' : global.qsConfig.show.coverForeground.template
                        .replace(/\{0\}/g, _.random(1, global.qsConfig.show.coverForeground.max))
                };
                next();
            }
        }
    ]
};

matcher.updateCover = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        async.waterfall([function(callback){
            if (req.session.matcherSave) {
                new Show(req.session.matcherSave).save(function(err, show){
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
        }], function(err, show) {
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
                    async.waterfall([
                        // Find item in same shop
                        function(callback) {
                            if (req.injection.itemRef.shopRef) {
                                _findRandomItem(category, {'shopRef' : req.injection.itemRef.shopRef}, callback);
                            } else {
                                callback(null, null);
                            }
                        },
                        // Find item remix only
                        function(item, callback) {
                            if (item) {
                                callback(null, item);
                            } else {
                                _findRandomItem(category, {'remix' : true}, callback);
                            }
                        },
                        // Find all items
                        function(item, callback) {
                            if (item) {
                                callback(null, item);
                            } else {
                                _findRandomItem(category, {}, callback);
                            }
                        }
                    ], callback);
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

                    req.session.shareTradeTargetInfo = {
                        'remix' : {
                            'master' : {
                                'itemRef' : req.injection.itemRef._id,
                                'rect' : data.master.rect
                            },
                            'slaves' : data.slaves.map(function(slave) {
                                return {
                                    'itemRef' : slave.itemRef._id,
                                    'rect' : slave.rect
                                };
                            })
                        }
                    };
                    break;
                }
            }
            next();
            
            // Register items as sync requested
            ItemSyncHelper.request(req.injection.itemRef);
            req.injection.remixItems.forEach(function(item, index) {
                ItemSyncHelper.request(item);
            });
        }
    ]
};

var _findRandomItem = function(category, criteria, callback) {
    criteria = _.extend({
        'categoryRef' : category._id,
        'delist' : null
    }, criteria);
    Item.find(criteria).count(function(err, count) {
        Item.find(criteria).populate('shopRef').skip(_.random(0, count - 1)).limit(1).exec(function(err, items) {
            callback(err, items[0]);
        });
    });
};
