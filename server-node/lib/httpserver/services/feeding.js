var Show = require('../../model/shows');
var ServiceUtil = require('../../util/servicesUtil');

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
    ServiceUtil.sendSingleQueryToResponse(res, buildQuery, additionFunc, _showDataGenFunc, pageNo, pageSize);
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
    ServiceUtil.sendSingleQueryToResponse(res, buildQuery, additionFunc, _showDataGenFunc, pageNo, pageSize);
}

//feeding/like sortedByNumLike
_like = function (req, res){
    var param, pageNo, pageSize;
    param = req.body;
    pageNo = param.pageNo || 1;
    pageSize = param.pageSize || 10;
    function buildQuery() {
        return Show.find();
    }
    function additionFunc(query) {
        query.sort({numLike: 1});
        _showPopulate(query);
        return query;
    }
    ServiceUtil.sendSingleQueryToResponse(res, buildQuery, additionFunc, _showDataGenFunc, pageNo, pageSize);
}
//feeding/choosen
//TODO
_choosen = function (req, res){
    return _like(req, res);
};

//feeding/byXXX
//byModel
_byModel = function (req, res) {
    var param, producerIDs, pageNo, pageSize;
    param = req.body;
    producerIDs = param.producerIDs || [];
    producerIDs = ServiceUtil.stringArrayToObjectIdArray(producerIDs);
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
    ServiceUtil.sendSingleQueryToResponse(res, buildQuery, additionFunc, _showDataGenFunc, pageNo, pageSize);
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
    ServiceUtil.sendSingleQueryToResponse(res, buildQuery, additionFunc, _showDataGenFunc, pageNo, pageSize);
};

//byBrand|brand string
//TODO
_byBrand = function (req, res){
    return _byTag(req, res);
};

//byFollow|_id string of ObjectId
//TODO
_byFollow = function (req, res){
    return _byTag(req, res);
};

module.exports = {
    'recommendation' : ['get', _recommendation],
    'hot' : ['get', _hot],
    'like' : ['get', _like],
    "choosen" : ['get', _choosen],

    'byModel' : ['get', _byModel],
    'byTag' : ['get', _byTag],
    'byBrand' : ['get', _byBrand],
    'byFollow' : ['get', _byFollow]
};
