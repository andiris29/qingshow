var Show = require('../../model/shows');

var _byRecommendation;
_byRecommendation = function (req, res) {
    var param, tags, pageNo, pageSize;
    param = req.body;
    tags = param.tags || [];
    pageNo = param.pageNo || 1;
    pageSize = param.pageSize || 10;


    Show.find({tags: {$in: tags}})
        .skip((pageNo - 1) * pageSize)
        .limit(pageSize)
        .sort({numLike: 1})
        .exec(function (err, shows) {
            if (err) {
                res.send('error');
            } else {
                var retData = {
                    metadata: {
                        //TODO change invilidateTime
                        invalidateTime: 30
                    },
                    data: {
                        shows: shows
                    }
                };
                res.json(retData);
            }
        });
};

module.exports = {
    'byRecommendation' : ['get', _byRecommendation]
};
