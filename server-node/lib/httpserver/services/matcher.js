var mongoose = require('mongoose');
var async = require('async');

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

matcher.queryCategory = {
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

        //if (!qsParam.category || !qsParam.category.length) {
        //    ResponseHelper.response(res, ServerError.NotEnoughParam);
        //    return;
        //}
        ServiceHelper.queryPaging(req, res, function(qsParam, callback) {
            var id = RequestHelper.parseId(qsParam.category);
            var criteria = {
                'categoryRef' : id
            }
            MongoHelper.queryPaging(Item.find(criteria), Item.find(criteria), qsParam.pageNo, qsParam.pageSize, callback);
        }, function(items) {
            // responseDataBuilder
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

        var show = new Show({
            'itemRefs' : itemRefs, 
            'ugc' : true
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
}

matcher.updateCover = {
    'method' : 'post',
    'permissionValidators' : ['loginValidator'],
    'func' : function(req, res) {
        var formidable = require('formidable');
        var path = require('path');

        var form = new formidable.IncomingForm();
        form.uploadDir = global.__qingshow_uploads.folder;
        form.keepExtensions = true;
        form.parse(req, function(err, fields, files) {
            if (err) {
                ResponseHelper.response(res, err);
                return;
            }
            if (!fields['_id'] || !fields['_id'].length) {
                ResponseHelper.response(res, ServerError.NotEnoughParam);
                return;
            }
            var file = files['cover'];
            if (!file) {
                ResponseHelper.response(res, ServerError.NotEnoughParam);
                return;
            }
            Show.findOne({
                '_id' : RequestHelper.parseId(fields['_id']),
                'ugc' : true
            }, function(err, show) {
                show.set('cover', global.__qingshow_uploads.path + '/' + path.relative(form.uploadDir, file.path));
                show.save(function(err, show) {
                    ResponseHelper.response(res, err, {
                        'show' : show
                    });
                });
            });
        });
        return;
    }
}
