var qsdb = require('../../runtime/qsdb');
var Show = require('../../model/show');

var _byRecommendation = function(req, res) {
	var param = req.body;
	var tags = param.tags || [];
	var pageNo = param.pageNo || 1;
	var pageSize = param.pageSize || 10;

	Show.find({tags:{$in:tags}})
		.skip((pageNo - 1) * pageSize)
		.limit(pageSize)
		.sort({numLike:1})
		.exec(function(err, shows){
			if (err){
				res.send('error');
			}
			else{
				var retData = 
				{
					metadata:{
						//TODO
						invalidateTime: 30
					},
					data:{
						shows:shows
					}
				};
				res.send(retData);
			}
	});
};

module.exports = {
    'byRecommendation' : ['get', _byRecommendation]
};
