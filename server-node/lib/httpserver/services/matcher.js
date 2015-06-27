var mongoose = require('mongoose');
var async = require('async');
var path = require('path');
var _ = require('underscore');

// model
var Category = require('../../model/categories');
var Item = require('../../model/items');
var Show = require('../../model/shows');
var RPeopleCreateShow = require('../../model/rPeopleCreateShow');

// util
var ResponseHelper = require('../helpers/ResponseHelper');
var RequestHelper = require('../helpers/RequestHelper');
var ServiceHelper = require('../helpers/ServiceHelper');
var MongoHelper = require('../helpers/MongoHelper.js');
var RelationshipHelper = require('../helpers/RelationshipHelper');

var ServerError = require('../server-error');

var matcher = module.exports;

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
        var qsParam = req.body;

        ServiceHelper.queryPaging(req, res, function(qsParam, callback) {
            var criteria = {
                'categoryRef' : RequestHelper.parseId(qsParam.categoryRef)
            };
            MongoHelper.queryRandom(Item.find(criteria), Item.find(criteria), qsParam.pageSize, callback);
        }, function(items) {
            return {
                'items' : items 
            };
        }, {});
    }
};

matcher.save = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        if (!req.body.itemRefs || !req.body.itemRefs.length) {
            ResponseHelper.response(res, ServerError.NotEnoughParam);
            return;
        }

        var itemRefs = RequestHelper.parseIds(req.body.itemRefs);

        var coverUrl = global.qsConfig.show.coverForeground.template;
        coverUrl = coverUrl.replace(/\{0\}/g, _.random(1, global.qsConfig.show.coverForeground.max));
        var show = new Show({
            'itemRefs' : itemRefs, 
            'ugc' : true,
            'coverForeground' : coverUrl
        });

        show.save(function(err, show) {
            if (err) {
                ResponseHelper.response(res, err);
            } else if (!show) {
                ResponseHelper.response(res, ServerError.ServerError);
            } else {
                var initiatorRef = req.qsCurrentUserId;
                var targetRef = show._id;
                RelationshipHelper.create(RPeopleCreateShow, initiatorRef, targetRef, function(err, relationship) {
                    if (err) {
                        ResponseHelper.response(res, err);
                    } else if (!relationship) {
                        ResponseHelper.response(res, ServerError.ServerError);
                    } else {
                        ResponseHelper.response(res, null, {
                            'show' : show
                        });
                    }
                });
            }
        });
    }
};

matcher.updateCover = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        RequestHelper.parseFile(req, global.qsConfig.uploads.show.cover.localPath, function(err, fields, file) {
            if (err) {
                ResponseHelper.response(res, err);
                return;
            }
            if (!fields['_id'] || !fields['_id'].length) {
                ResponseHelper.response(res, ServerError.NotEnoughParam);
                return;
            }
            if (!file) {
                ResponseHelper.response(res, ServerError.NotEnoughParam);
                return;
            }
            Show.findOne({
                '_id' : RequestHelper.parseId(fields['_id']),
                'ugc' : true
            }, function(err, show) {
                show.set('cover', global.qsConfig.uploads.show.cover.exposeToUrl + '/' + path.relative(global.qsConfig.uploads.show.cover.localPath, file.path));
                show.save(function(err, show) {
                    ResponseHelper.response(res, err, {
                        'show' : show
                    });
                });
            });
        });
        return;
    }
};
