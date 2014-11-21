//Model
var People = require('../../model/peoples');
var Comment = require('../../model/comments');
var Show = require('../../model/shows');
var Brand = require('../../model/brands');
var PItem = require('../../model/pItems');

var mongoose = require('mongoose');

//Utils
var ServicesUtil = require('../servicesUtil');
var ServerError = require('../server-error');
var nimble = require('nimble');



var _models, _comments, _brands, _terms, _pItemsByCategories;
_models = function (req, res) {
    var param = res.queryString;
    if (param._ids) {
        //TODO Check hasFollowed
        try {
            var ids = param._ids.split(',');
            var idsObjArray = ServicesUtil.stringArrayToObjectIdArray(ids);
        } catch (e) {
            ServicesUtil.responseError(res, new ServerError(ServerError.PeopleNotExist));
            return;
        }

        People.find({_id : {$in : idsObjArray}}, function (err, peoples){
            if (err) {
                ServicesUtil.responseError(res, err);
                return;
            } else if (!peoples || !peoples.length) {
                ServicesUtil.responseError(res, ServerError(ServerError.PeopleNotExist));
                return;
            } else {
                var retObj = {
                    metadata: {
                        "numPages": 1,
                        "numTotal": peoples.length,
                        "invalidateTime": 3600000
                    },
                    data: {
                        peoples: peoples
                    }
                };
                res.json(retObj);
                return;
            }
        });
    } else {
        var followingList = null;
        nimble.series([function (callback) {
            if (req.session.userId) {
                People.findOne({_id : req.session.userId})
                    .select('followRefs')
                    .exec(function (err, p) {
                        if (p) {
                            followingList = p.followRefs;
                        }
                        callback();
                    });
            } else {
                callback();
            }

        }, function (callback) {
            var pageNo = param.pageNo || 1;
            var pageSize = param.pageSize || 10;
            function buildQuery() {
                return People.find({roles : 1});
            }
            function modelDataGenFunc(data) {
                return {
                    peoples: data
                };
            }
            ServicesUtil.sendSingleQueryToResponse(res, buildQuery, null, modelDataGenFunc, pageNo, pageSize, function (models, cb) {
                var retModels = [];
                models.forEach(function (model) {
                    var m = JSON.parse(JSON.stringify(model));
                    if (followingList && followingList.indexOf(model._id) !== -1) {
                        m.hasFollowed = true;
                    } else {
                        m.hasFollowed = false;
                    }
                    retModels.push(m);
                });
                cb(retModels);
            });
            callback();
        }]);
    }
};

_brands = function (req, res) {
    var param, pageNo, pageSize, type;
    try {
        param = res.queryString;
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
        return Brand.find({type : type});
    }
    function modelDataGenFunc(data) {
        return {
            brands: data
        };
    }
    ServicesUtil.sendSingleQueryToResponse(res, buildQuery, null, modelDataGenFunc, pageNo, pageSize);
};

_comments = function (req, res) {
    try {
        var param = res.queryString;
        var pageNo = parseInt(param.pageNo || 1);
        var pageSize = parseInt(param.pageSize || 10);
        var showIdStr = param.showId;
        var showIdObj = mongoose.mongo.BSONPure.ObjectID(showIdStr);
    } catch (e) {
        ServicesUtil.responseError(res, new ServerError(ServerError.ShowNotExist));
        return;
    }
    Show.findOne({_id : showIdObj}, function (err, show) {
        function buildQuery() {
            return Comment.find({showRef : showIdObj});
        }
        function additionFunc(query) {
            query.sort({time: 1})
                .populate('peopleRef');
        }
        function commentDataGenFunc(data) {
            return {
                comments: data
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

_terms = function (req, res) {
    //TODO 暂时不做
    res.send('terms');
};

/*
* query/pItemsByCategories [paging][get]

 Request
 categories array of code

 Response
 data.items array, entity in db.pItems
* */

_pItemsByCategories = function (req, res) {
    try {
        var param = req.queryString;
        var pageNo = parseInt(param.pageNo || 1);
        var pageSize = parseInt(param.pageSize || 20);
        var categories = (param.categories || '').split(',');
    } catch (e) {
        ServicesUtil.responseError(res, e);
        return;
    }
    function buildQuery() {
        var query = PItem.find();
        query.where({category : {$in : categories}});
        return query;
    }
    function additionFunc(query) {
        //TODO: add sort?
        return query;
    }
    function dateGenFunc(datas) {
        return {
            pItems: datas
        };
    }
    ServicesUtil.sendSingleQueryToResponse(res, buildQuery, additionFunc, dateGenFunc, pageNo, pageSize);
};

module.exports = {
    'models' : {method: 'get', func: _models, needLogin: false},
    'comments' : {method: 'get', func: _comments, needLogin: false},
    'brands' : {method: 'get', func:_brands, needLogin: false},
    'terms' : {method: 'get', func: _terms, needLogin: false},
    "pItemsByCategories" : {method: 'get', func: _pItemsByCategories, needLogin: false}
};