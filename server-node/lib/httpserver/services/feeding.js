//model
var Show = require('../../model/shows');
var Brand = require('../../model/brands');
var Item = require('../../model/items');
var People = require('../../model/peoples');
var Chosen = require('../../model/chosens');
//util
var ServicesUtil = require('../servicesUtil');
var ServerError = require('../server-error');
var mongoose = require('mongoose');
var nimble = require('nimble');
var _recommendation, _hot, _like, _chosen;
var _byModel, _byTag, _byBrand, _byBrandDiscount, _byStudio, _byFollow;

//Utility for feeding service
function _showPopulate(query) {
    query.populate({path: "modelRef"})
        .populate("itemRefs")
    return query;
}
function _showDataGenFunc(data) {
    return {
        shows: data
    };
}

function showsFinalHandler(shows, callBackFunc) {
    nimble.series([
        function (callback) {
            var funcArray = [];
            shows.forEach(function (show) {
                var updateFunc = function (callback) {
                    show.updateCoverMetaData(callback);
                };
                funcArray.push(updateFunc);
            });
            nimble.parallel(funcArray, callback);
        },
        function (callback) {
            callBackFunc(shows);
            callback();
        }
    ]);
}


//feeding/xxx
//feeding/recommendation
_recommendation = function (req, res) {
    var param, tags, pageNo, pageSize;
    param = res.queryString;
    tags = param.tags || [];
    pageNo = parseInt(param.pageNo || 1);
    pageSize = parseInt(param.pageSize || 10);

    function buildQuery() {
        var query = Show.find();
        if (tags.length) {
            query.where({tags: {$in: tags}});
        }
        return query;
    }
    function additionFunc(query) {
        query.sort({numLike: 1});
        _showPopulate(query);
        return query;
    }
    ServicesUtil.sendSingleQueryToResponse(res, buildQuery, additionFunc, _showDataGenFunc, pageNo, pageSize, showsFinalHandler);
};

//feeding/hot  sortedByNumView
_hot = function (req, res) {
    var param, pageNo, pageSize;
    param = res.queryString;
    pageNo = parseInt(param.pageNo || 1);
    pageSize = parseInt(param.pageSize || 10);
    function buildQuery() {
        return Show.find();
    }
    function additionFunc(query) {
        query.sort({numView: 1});
        _showPopulate(query);
        return query;
    }
    ServicesUtil.sendSingleQueryToResponse(res, buildQuery, additionFunc, _showDataGenFunc, pageNo, pageSize, showsFinalHandler);
}

//feeding/like sortedByNumLike
_like = function (req, res){
    var param, pageNo, pageSize;
    param = res.queryString;
    pageNo = parseInt(param.pageNo || 1);
    pageSize = parseInt(param.pageSize || 10);
    var currentUser = req.currentUser;
    People.findOne({_id : currentUser._id})
        .select('likingShowRefs')
        .exec(function (err, p){
            if (err) {
                ServicesUtil.responseError(res, err);
                return;
            } else if (!p) {
                ServicesUtil.responseError(res, new ServerError(ServerError.PeopleNotExist));
                return;
            } else {
                var count = p.likingShowRefs.length;
                if ((pageNo - 1) * pageSize > count){
                    ServicesUtil.responseError(res, new ServerError(ServerError.PagingNotExist));
                    return;
                }
//                Show.populate(chosen.showRefs, {path :'modelRef itemRefs'})
                People.populate(p,
                    {
                        path : 'likingShowRefs',
                        option : {
                            skip: (pageNo - 1) * pageSize,
                            limit: pageSize}
                    },
                    function (err, populatedPeople) {
                        if (err) {
                            ServicesUtil.responseError(res, err);
                            return;
                        } else if ( !populatedPeople) {
                            ServicesUtil.responseError(res, new ServerError(ServerError.PeopleNotExist));
                            return;
                        } else {

                            Show.populate(populatedPeople.likingShowRefs, {
                                path :'modelRef itemRefs'
                            },
                                function (err, shows) {
                                    if (err) {
                                        ServicesUtil.responseError(res, err);
                                        return;
                                    } else if ( !shows) {
                                        ServicesUtil.responseError(res, new ServerError(ServerError.PeopleNotExist));
                                        return;
                                    } else {
                                        var retData = {
                                            metadata: {
                                                "numTotal": count,
                                                "numPages": parseInt((count + pageSize - 1) / pageSize),
                                            },
                                            data: {
                                                shows: populatedPeople.likingShowRefs
                                            }
                                        };
                                        res.json(retData);
                                        return;
                                    }
                                });
                        }
                });
            }
        });
};

//feeding/chosen
_chosen = function (req, res){
    var param, pageNo, pageSize;
    param = res.queryString;
    pageNo = parseInt(param.pageNo || 1);
    pageSize = parseInt(param.pageSize || 10);

    Chosen.find()
        .where('dateStart').lte(Date.now())
        .sort({dateStart : 1})
        .limit(1)
        .exec(function (err, chosens) {
            if (err) {
                ServicesUtil.responseError(res, err);
                return;
            } else if (!chosens || !chosens.length) {
                ServicesUtil.responseError(res, new ServerError(ServerError.ShowNotExist));
                return;
            } else {
                var chosen = chosens[0];
                var count = chosen.showRefs.length;
                if ((pageNo - 1) * pageSize > count){
                    ServicesUtil.responseError(res, new ServerError(ServerError.PagingNotExist));
                    return;
                }
                Chosen.populate(chosen, {
                    path: 'showRefs',
                    options: {
                        skip: (pageNo - 1) * pageSize,
                        limit: pageSize
                    }
                }, function (err, chosen){
                    if (err) {
                        ServicesUtil.responseError(res, err);
                        return;
                    } else if (!chosen) {
                        ServicesUtil.responseError(res, new ServerError(ServerError.ShowNotExist));
                        return;
                    } else {
                        Show.populate(chosen.showRefs, 'modelRef itemRefs', function(err, shows){
                            if (err) {
                                ServicesUtil.responseError(res, err);
                                return;
                            } else {
                                var retData = {
                                    metadata: {
                                        "numTotal": count,
                                        "numPages": parseInt((count + pageSize - 1) / pageSize),
                                        "refreshTime": 3600000
                                    },
                                    data: {
                                        shows: shows
                                    }
                                };
                                res.json(retData);
                                return;
                            }
                        });
                    }
                });
            }
        });
};

//feeding/byXXX
//byModel
_byModel = function (req, res) {
    var param, producerIDs, pageNo, pageSize;
    param = res.queryString;
    producerIDs = param.producerIDs || [];
    producerIDs = ServicesUtil.stringArrayToObjectIdArray(producerIDs);
    pageNo = parseInt(param.pageNo || 1);
    pageSize = parseInt(param.pageSize || 10);
    function buildQuery() {
        var query = Show.find();
        if (producerIDs.length) {
            query.where({modelRef: {$in: producerIDs}});
        }
        return query;
    }
    function additionFunc(query) {
        query.sort({numLike: 1});
        _showPopulate(query);
        return query;
    }
    ServicesUtil.sendSingleQueryToResponse(res, buildQuery, additionFunc, _showDataGenFunc, pageNo, pageSize, showsFinalHandler);
};

//byTag
_byTag = function (req, res){
    var param, tags, pageNo, pageSize;
    param = res.queryString;
    tags = param.tags || [];

    if (tags.length === 0){
        ServicesUtil.responseError(res, new ServerError(ServerError.NotEnoughParam));
        return;
    }

    pageNo = parseInt(param.pageNo || 1);
    pageSize = parseInt(param.pageSize || 10);

    function buildQuery() {
        var query = Show.find();
        if (tags.length) {
            query.where({tags: {$in: tags}});
        }
        return query;
    }
    function additionFunc(query) {
        query.sort({numLike: 1});
        _showPopulate(query);
        return query;
    }
    ServicesUtil.sendSingleQueryToResponse(res, buildQuery, additionFunc, _showDataGenFunc, pageNo, pageSize, showsFinalHandler);
};

//byBrand|_id string
_byBrand = function (req, res){
    var param, brandIdStr, brandIdObj, pageNo, pageSize;
    try {
        param = res.queryString;
        brandIdStr = param._id || [];
        brandIdObj = mongoose.mongo.BSONPure.ObjectID(brandIdStr);
        pageNo = parseInt(param.pageNo || 1);
        pageSize = parseInt(param.pageSize || 10);
    } catch (e) {
        ServicesUtil.responseError(res, new ServerError(ServerError.BrandNotExist));
        return;
    }
    Brand.findOne({_id: brandIdObj}, function (err, brand){
        if (err) {
            ServicesUtil.responseError(res, err);
            return;
        } else if (!brand) {
            ServicesUtil.responseError(res, new ServerError(ServerError.BrandNotExist));
            return;
        } else {
            //find items
            Item.find({brandRef: brandIdObj}, function (err, items){
                if (err) {
                    ServicesUtil.responseError(res, err);
                    return;
                } else if (!items || !items.length) {
                    ServicesUtil.responseError(res, new ServerError(ServerError.ShowNotExist));
                    return;
                } else {
                    var itemsIdArray = [];
                    items.forEach(function (item){
                        itemsIdArray.push(item._id);
                    });
                    function buildQuery(){
                        var query = Show.find({itemRefs : {$in : itemsIdArray}});
                        return query;
                    }
                    ServicesUtil.sendSingleQueryToResponse(res, buildQuery, _showPopulate, _showDataGenFunc, pageNo, pageSize, showsFinalHandler);
                }
            });
        }
    });
};

_byBrandDiscount = function (req, res) {
    var param, brandIdStr, brandIdObj, pageNo, pageSize;
    try {
        param = res.queryString;
        brandIdStr = param._id || [];
        brandIdObj = mongoose.mongo.BSONPure.ObjectID(brandIdStr);
        pageNo = parseInt(param.pageNo || 1);
        pageSize = parseInt(param.pageSize || 10);
    } catch (e) {
        ServicesUtil.responseError(res, new ServerError(ServerError.BrandNotExist));
    }
    var brand = null;
    var error = null;
    var brandItems = null;
    nimble.series([
        function (callback) {
            Brand.findOne({_id: brandIdObj}, function (err, b) {
                error = err
                if (!b) {
                    error = new ServerError(ServerError.BrandNotExist);
                } else {
                    brand = b;
                }
                callback();
            });
        },function (callback) {
            if (error) {
                callback();
                return;
            } else {
                Item.find({brandRef: brandIdObj}, function (err, items) {
                    if (err) {
                        error = err;
                    } else if (!items || !items.length) {
                        error = new ServerError(ServerError.ItemNotExist);
                    } else {
                        brandItems = items;
                    }
                    callback();
                });
            }
        }, function (callback) {
            if (error) {
                ServicesUtil.responseError(res, error);
                callback();
                return;
            } else {
                var itemsIdArray = [];
                brandItems.forEach(function (item){
                    itemsIdArray.push(item._id);
                });
                function buildQuery(){
                    var query = Show.find({itemRefs : {$in : itemsIdArray}});
                    query.where('discountInfo').exists(true);
                    return query;
                }
                function additionFunc(query) {
                    query.sort({"discountInfo.start": -1});
                    _showPopulate(query);
                    return query;
                }
                ServicesUtil.sendSingleQueryToResponse(res, buildQuery, additionFunc, _showDataGenFunc, pageNo, pageSize, showsFinalHandler, callback);
            }
        }]);
};

_byStudio = function (req, res) {
    var param, pageNo, pageSize;
    try {
        param = res.queryString;
        pageNo = parseInt(param.pageNo || 1);
        pageSize = parseInt(param.pageSize || 10);
    } catch (e) {
        ServicesUtil.responseError(res, new ServerError(ServerError.BrandNotExist));
    }
    Brand.find({type: 1}, function (err, brands){
        if (err) {
            ServicesUtil.responseError(res, err);
            return;
        } else if (!brands) {
            ServicesUtil.responseError(res, new ServerError(ServerError.BrandNotExist));
            return;
        } else {

            var brandIdArray = [];
            brands.forEach(function (brand) {
                brandIdArray.push(brand._id);
            });

            //find items
            Item.find({brandRef: {$in : brandIdArray}}, function (err, items){
                if (err) {
                    ServicesUtil.responseError(res, err);
                    return;
                } else if (!items || !items.length) {
                    ServicesUtil.responseError(res, ServerError(res, new ServerError(ServerError.ShowNotExist)));
                    return;
                } else {
                    var itemsIdArray = [];
                    items.forEach(function (item){
                        itemsIdArray.push(item._id);
                    });
                    function buildQuery(){
                        var query = Show.find({itemRefs : {$in : itemsIdArray}});
                        return query;
                    }
                    ServicesUtil.sendSingleQueryToResponse(res, buildQuery, _showPopulate, _showDataGenFunc, pageNo, pageSize, showsFinalHandler);
                }
            });
        }
    });
}



//byFollow|_id string of ObjectId
_byFollow = function (req, res){
    var param, peopleIdStr, peopleIdObj, pageNo, pageSize;
    try {
        param = res.queryString;
        peopleIdStr = param._id || [];
        peopleIdObj = mongoose.mongo.BSONPure.ObjectID(peopleIdStr);
        pageNo = parseInt(param.pageNo || 1);
        pageSize = parseInt(param.pageSize || 10);
    } catch (e) {
        ServicesUtil.responseError(res, new ServerError(ServerError.PeopleNotExist));
        return;
    }

    People.findOne({_id : peopleIdObj})
        .select('followRefs')
        .exec(function (err, people) {
            function buildQuery(){
                var query = Show.find();
                var followsIdArray;
                if (!people.followRefs || !people.followRefs.length) {
                    followsIdArray = [];
                } else {
                    followsIdArray = people.followRefs;
                }
                query.where({modelRef: {$in: followsIdArray}});
                return query;
            };
            function additionFunc(query) {
//                    query.sort({numLike: 1});
                _showPopulate(query);
                return query;
            }
            if (err) {
                ServicesUtil.responseError(res, err);
                return;
            } else if (!people) {
                ServicesUtil.responseError(res, new ServerError(ServerError.PeopleNotExist));
                return;
            } else {
                ServicesUtil.sendSingleQueryToResponse(res, buildQuery, additionFunc, _showDataGenFunc, pageNo, pageSize, showsFinalHandler);
            }
    });
//    return _byTag(req, res);
};

module.exports = {
    'recommendation' : {method: 'get', func: _recommendation},
    'hot' : {method: 'get', func: _hot},
    'like' : {method: 'get', func: _like, needLogin: true},
    "chosen" : {method: 'get', func: _chosen},

    'byModel' : {method: 'get',func: _byModel},
    'byTag' : {method: 'get', func: _byTag},
    'byBrand' : {method: 'get', func: _byBrand},
    'byBrandDiscount': {method: 'get', func: _byBrandDiscount},
    'byStudio' : {method:'get', func: _byStudio},
    'byFollow' : {method: 'get', func: _byFollow}
};
