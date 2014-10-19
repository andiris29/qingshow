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

var _recommendation, _hot, _like, _choosen;
var _byModel, _byTag, _byBrand, _byFollow;

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


//feeding/xxx
//feeding/recommendation
_recommendation = function (req, res) {
    var param, tags, pageNo, pageSize;
    param = req.body;
    tags = param.tags || [];
    pageNo = param.pageNo || 1;
    pageSize = param.pageSize || 10;

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
    ServicesUtil.sendSingleQueryToResponse(res, buildQuery, additionFunc, _showDataGenFunc, pageNo, pageSize);
};

//feeding/hot  sortedByNumView
_hot = function (req, res) {
    var param, pageNo, pageSize;
    param = req.body;
    pageNo = param.pageNo || 1;
    pageSize = param.pageSize || 10;
    function buildQuery() {
        return Show.find();
    }
    function additionFunc(query) {
        query.sort({numView: 1});
        _showPopulate(query);
        return query;
    }
    ServicesUtil.sendSingleQueryToResponse(res, buildQuery, additionFunc, _showDataGenFunc, pageNo, pageSize);
}

//feeding/like sortedByNumLike
_like = function (req, res){
    var param, pageNo, pageSize;
    param = req.body;
    pageNo = param.pageNo || 1;
    pageSize = param.pageSize || 10;
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
                                                "numPages": (count + pageSize - 1) / pageSize,
                                                "refreshTime": 3600000
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

//feeding/choosen
_choosen = function (req, res){
    var param, pageNo, pageSize;
    param = req.body;
    pageNo = param.pageNo || 1;
    pageSize = param.pageSize || 10;

    Chosen.find()
        .where('dateStart').lte(Date.now())
        .sort({dateStart : 1})
        .limit(1)
        .populate({
            path: 'showRefs',
            options: {
                skip: (pageNo - 1) * pageSize,
                limit: pageSize
            }
        })
        .exec(function (err, chosens) {
            if (err) {
                ServicesUtil.responseError(res, err);
                return;
            } else if (!chosens || !chosens.length) {
                ServicesUtil.responseError(res, new ServerError(ServerError.ShowNotExist));
                return;
            } else {
                var chosen = chosens[0];
//                chosen.showRefs.populdate('modelRef');
                Show.populate(chosen.showRefs, 'modelRef itemRefs', function(err, shows){
                    if (err) {
                        ServicesUtil.responseError(res, err);
                        return;
                    } else {
                        res.json(shows);
                        return;
                    }
                });
            }
        });
};

//feeding/byXXX
//byModel
_byModel = function (req, res) {
    var param, producerIDs, pageNo, pageSize;
    param = req.body;
    producerIDs = param.producerIDs || [];
    producerIDs = ServicesUtil.stringArrayToObjectIdArray(producerIDs);
    pageNo = param.pageNo || 1;
    pageSize = param.pageSize || 10;
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
    ServicesUtil.sendSingleQueryToResponse(res, buildQuery, additionFunc, _showDataGenFunc, pageNo, pageSize);
//    _sendShowsQueryToResponse(res, buildQuery, pageNo, pageSize);
};

//byTag
_byTag = function (req, res){
    var param, tags, pageNo, pageSize;
    param = req.body;
    tags = param.tags || [];
    pageNo = param.pageNo || 1;
    pageSize = param.pageSize || 10;

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
    ServicesUtil.sendSingleQueryToResponse(res, buildQuery, additionFunc, _showDataGenFunc, pageNo, pageSize);
};

//byBrand|_id string
_byBrand = function (req, res){
    var param, brandIdStr, brandIdObj, pageNo, pageSize;
    try {
        param = req.body;
        brandIdStr = param._id || [];
        brandIdObj = mongoose.mongo.BSONPure.ObjectID(brandIdStr);
        pageNo = param.pageNo || 1;
        pageSize = param.pageSize || 10;
    } catch (e) {
        ServicesUtil.responseError(res, new ServerError(ServerError.BrandNotExist));
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
                    ServicesUtil.responseError(res, ServerError(res, ServerError.ShowNotExist));
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
                    ServicesUtil.sendSingleQueryToResponse(res, buildQuery, _showPopulate, _showDataGenFunc, pageNo, pageSize);
                }
            });
        }
    });

};

//byFollow|_id string of ObjectId
_byFollow = function (req, res){
    var param, peopleIdStr, peopleIdObj, pageNo, pageSize;
    try {
        param = req.body;
        peopleIdStr = param._id || [];
        peopleIdObj = mongoose.mongo.BSONPure.ObjectID(peopleIdStr);
        pageNo = param.pageNo || 1;
        pageSize = param.pageSize || 10;
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
                ServicesUtil.sendSingleQueryToResponse(res, buildQuery, additionFunc, _showDataGenFunc, pageNo, pageSize);
            }
    });
//    return _byTag(req, res);
};

module.exports = {
    'recommendation' : {method: 'get', func: _recommendation},
    'hot' : {method: 'get', func: _hot},
    'like' : {method: 'get', func: _like, needLogin: true},
    "choosen" : {method: 'get', func: _choosen},

    'byModel' : {method: 'get',func: _byModel},
    'byTag' : {method: 'get', func: _byTag},
    'byBrand' : {method: 'get', func: _byBrand},
    'byFollow' : {method: 'get', func: _byFollow}
};
