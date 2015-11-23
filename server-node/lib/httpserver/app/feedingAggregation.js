// ------------------
// Import
// ------------------
var Show = require('../../dbmodels').Show,
    ShowCode = require('../../dbmodels').ShowCode,
    People = require('../../dbmodels').People;

var RequestHelper = require('../../helpers/RequestHelper'),
    ResponseHelper = require('../../helpers/ResponseHelper');

var errors = require('../../errors');

// ------------------
// Privates
// ------------------
var _buildFeaturedCriteria = function(req, featuredRank) {
    var criteria = [
        {'featuredRank' : featuredRank}
    ];
    if (req.queryString.from) {
        criteria.push({'create' : {'$gte' : RequestHelper.parseDate(req.queryString.from)}});
    }
    if (req.queryString.to) {
        criteria.push({'create' : {'$lt' : RequestHelper.parseDate(req.queryString.to)}});
    }
    return {
        '$and' : criteria
    };
};

var _owners = function(req, res, next) {
    Show.aggregate([
        {'$match' : req.injection.criteria}, 
        {'$group' : {
            '_id' : '$ownerRef',
            'numView' : {'$sum' : '$numView'}
        }}, 
        {'$sort' : {'numView' : -1}}
    ], function(err, results) {
        if (err) {
            next(errors.genUnkownError(err));
        } else {
            var topOwners = [],
                indexOfCurrentUser = -1;
            results.forEach(function(result, index) {
                if (topOwners.length < 8) {
                    topOwners.push({'ownerRef' : result._id});
                }
                if (req.qsCurrentUserId && 
                    req.qsCurrentUserId.toString() === result._id.toString()) {
                    indexOfCurrentUser = index;
                }
            });
            
            People.populate(topOwners, {
                'path' : 'ownerRef',
                'model' : 'peoples'
            }, function(err, topOwners) {
                if (err) {
                    next(errors.genUnkownError(err));
                } else {
                    topOwners = topOwners.map(function(object) {
                        return object.ownerRef;
                    });
                    ResponseHelper.write(res, {
                        'indexOfCurrentUser' : indexOfCurrentUser,
                        'numOwners' : results.length
                    }, {
                        'topOwners' : topOwners
                    });
                    next();
                }
            });
        }
    });
};


// ------------------
// Services
// ------------------
var feedingAggregation = module.exports;

feedingAggregation.matchNew = {
    'method' : 'get',
    'func' : [
    ]
};

feedingAggregation.featuredTopOwners = {
    'method' : 'get',
    'func' : [
        function(req, res, next) {
            req.injection.criteria = _buildFeaturedCriteria(req, ShowCode.FEATURED_RANK_TALENT);
            next();
        },
        _owners
    ]
};

feedingAggregation.matchHotTopOwners = {
    'method' : 'get',
    'func' : [
        function(req, res, next) {
            req.injection.criteria = _buildFeaturedCriteria(req, ShowCode.FEATURED_RANK_HOT);
            next();
        },
        _owners
    ]
};

feedingAggregation.matchNewTopOwners = {
    'method' : 'get',
    'func' : [
        function(req, res, next) {
            req.injection.criteria = _buildFeaturedCriteria(req, ShowCode.FEATURED_RANK_NEW);
            next();
        },
        _owners
    ]
};
