var Show = require('../../model/shows');
var ServiceUtil = require('../../util/servicesUtil');

var _byRecommendation;
var _byProducer;

var _sendShowsQueryToResponse;

//Utility for feeding service
function _showPopulate(query) {
    query.populate({path: "producerRef"})
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
_byRecommendation = function (req, res) {
    var param, tags, pageNo, pageSize, query;
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

//feeding/byXXX
_byProducer = function (req, res) {
    var param, producerIDs, pageNo, pageSize, query;
    param = req.body;
    producerIDs = param.producerIDs || [];
    producerIDs = ServiceUtil.stringArrayToObjectIdArray(producerIDs);
    pageNo = param.pageNo || 1;
    pageSize = param.pageSize || 10;
    function buildQuery() {
        query = Show.find();
        if (producerIDs.length) {
            query.where({producerRef: {$in: producerIDs}});
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

module.exports = {
    'byRecommendation' : ['get', _byRecommendation],
    'byProducer' : ['get', _byProducer]
};
