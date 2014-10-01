var qsdb = require('../../runtime/qsdb');

var _byRecommendation = function(req, res) {
    qsdb.connect(function(err, db) {
    });
};

module.exports = {
    'byRecommendation' : ['get', _byRecommendation]
};
