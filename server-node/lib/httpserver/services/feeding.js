var Show = require('../../model/shows');


var _byRecommendation;
_byRecommendation = function (req, res) {
    var param, tags, pageNo, pageSize, query;
    param = req.body;
    tags = param.tags || [];
    pageNo = param.pageNo || 1;
    pageSize = param.pageSize || 10;

    function buildQuery(){
        query = Show.find();
        if (tags.length){
            query.where({tags: {$in: tags}});
        }
        return query;
    }

    query = buildQuery();
    query.count(function(err, count){
        var numPages = parseInt((count + pageSize - 1) / pageSize);
        query = buildQuery();

        query.skip((pageNo - 1) * pageSize)
            .limit(pageSize)
            .sort({numLike: 1})
            .populate("producerRef")
            .populate("itemRefs")
            .exec(function (err, shows) {
                if (err) {
                    res.send('error');
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


};

module.exports = {
    'byRecommendation' : ['get', _byRecommendation]
};
