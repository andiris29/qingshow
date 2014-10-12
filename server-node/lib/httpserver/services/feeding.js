var Show = require('../../model/shows');
var ServiceUtil = require('../../util/servicesUtil');

var _byRecommendation;
var _byProducer;

var _sendShowsQueryToResponse;

_sendShowsQueryToResponse = function(res, queryFunc, pageNo, pageSize){
    var query;
    query = queryFunc();
    query.count(function (err, count) {
        if (err) {
            console.log(err);
            res.send("err");
            return;
        }
        var numPages = parseInt((count + pageSize - 1) / pageSize);
        query = queryFunc();
        ServiceUtil.limitQuery(query, pageNo, pageSize);

        query.sort({numLike: 1})
            .populate({path: "producerRef"})
            .populate("itemRefs")
            .exec(function (err, shows) {
                if (err) {
                    console.log(err);
                    res.send("err");
                } else {
                    var retData = {
                        metadata: {
                            //TODO change invilidateTime
                            "numPages": numPages,
                            "invalidateTime": 3600000
                        },
                        data: {
                            shows: shows
                        }
                    };
                    res.json(retData);
                }
            });
    });
}

_byRecommendation = function (req, res) {
    var param, tags, pageNo, pageSize, query;
    param = req.body;
    tags = param.tags || [];
    pageNo = param.pageNo || 1;
    pageSize = param.pageSize || 10;

    function buildQuery() {
        query = Show.find();
        if (tags.length) {
            query.where({tags: {$in: tags}});
        }
        return query;
    }
    _sendShowsQueryToResponse(res, buildQuery, pageNo, pageSize);
};

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
    _sendShowsQueryToResponse(res, buildQuery, pageNo, pageSize);
};

module.exports = {
    'byRecommendation' : ['get', _byRecommendation],
    'byProducer' : ['get', _byProducer]
};
