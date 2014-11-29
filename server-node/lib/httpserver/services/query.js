var mongoose = require('mongoose');
var async = require('async');
//Model
var People = require('../../model/peoples');
var Comment = require('../../model/comments');
var Show = require('../../model/shows');
var Brand = require('../../model/brands');
var PItem = require('../../model/pItems');
var PShow = require('../../model/pShows');
//Utils
var ServicesUtil = require('../servicesUtil');
var ServerError = require('../server-error');
var nimble = require('nimble');

var _shows = function(req, res) {
    var param = req.queryString;
    try {
        var _ids = ServicesUtil.stringArrayToObjectIdArray(param._ids.split(','));
    } catch (e) {
        ServicesUtil.responseError(res, new ServerError(ServerError.RequestValidationFail));
        return;
    }
    async.waterfall([
    function(callback) {
        Show.find({
            '_id' : {
                '$in' : _ids
            }
        }).populate('modelRef').populate('itemRefs').populate('itemRefs').exec(callback);
    },
    function(shows, callback) {

        Show.populate(shows, {
            'path' : 'itemRefs.brandRef',
            'model' : 'brands'
        }, callback);
    }], function(err, shows) {
        if (err) {
            ServicesUtil.responseError(res, err);
        } else {
            res.json(shows);
        }
    });
};

var _models, _comments, _brands;
_models = function(req, res) {
    var param = req.queryString;
    try {
        var ids = param._ids.split(',');
        var idsObjArray = ServicesUtil.stringArrayToObjectIdArray(ids);
    } catch (e) {
        ServicesUtil.responseError(res, new ServerError(ServerError.RequestValidationFail));
        return;
    }

    People.find({
        _id : {
            $in : idsObjArray
        }
    }, function(err, peoples) {
        if (err) {
            ServicesUtil.responseError(res, err);
            return;
        } else if (!peoples || !peoples.length) {
            ServicesUtil.responseError(res, ServerError(ServerError.PeopleNotExist));
            return;
        } else {
            res.json({
                data : {
                    peoples : peoples
                }
            });
            return;
        }
    });
};

_brands = function(req, res) {
    var param, pageNo, pageSize, type;
    try {
        param = req.queryString;
        pageNo = parseInt(param.pageNo || 1);
        pageSize = parseInt(param.pageSize || 10);
        type = param.type;
        if (type === 'brand') {
            type = 0;
        } else if (type === 'studio') {
            type = 1;
        }
    } catch (e) {
        ServicesUtil.responseError(res, new ServerError(ServerError.ShowNotExist));
        return;
    }
    if (type === undefined) {
        ServicesUtil.responseError(res, new ServerError(ServerError.NotEnoughParam));
        return;
    }

    function buildQuery() {
        return Brand.find({
            type : type
        });
    }

    function modelDataGenFunc(data) {
        return {
            brands : data
        };
    }


    ServicesUtil.sendSingleQueryToResponse(res, buildQuery, null, modelDataGenFunc, pageNo, pageSize);
};

_comments = function(req, res) {
    try {
        var param = req.queryString;
        var pageNo = parseInt(param.pageNo || 1);
        var pageSize = parseInt(param.pageSize || 10);
        var showIdStr = param.showId;
        var showIdObj = mongoose.mongo.BSONPure.ObjectID(showIdStr);
    } catch (e) {
        ServicesUtil.responseError(res, new ServerError(ServerError.ShowNotExist));
        return;
    }
    Show.findOne({
        _id : showIdObj
    }, function(err, show) {
        function buildQuery() {
            return Comment.find({
                showRef : showIdObj
            });
        }

        function additionFunc(query) {
            query.sort({
                time : 1
            }).populate('peopleRef');
        }

        function commentDataGenFunc(data) {
            return {
                comments : data
            };
        }

        if (err) {
            ServicesUtil.responseError(res, err);
        } else if (!show) {
            ServicesUtil.responseError(res, new ServerError(ServerError.ShowNotExist));
        } else {
            ServicesUtil.sendSingleQueryToResponse(res, buildQuery, additionFunc, commentDataGenFunc, pageNo, pageSize);
        }
    });
};

module.exports = {
    'shows' : {
        method : 'get',
        func : _shows,
        needLogin : false
    },
    'models' : {
        method : 'get',
        func : _models,
        needLogin : false
    },
    'comments' : {
        method : 'get',
        func : _comments,
        needLogin : false
    },
    'brands' : {
        method : 'get',
        func : _brands,
        needLogin : false
    }
};
