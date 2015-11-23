// ------------------
// Import
// ------------------
var async = require('async');

var Show = require('../../dbmodels').Show,
    ShowCode = require('../../dbmodels').ShowCode,
    People = require('../../dbmodels').People;

var RequestHelper = require('../../helpers/RequestHelper'),
    ResponseHelper = require('../../helpers/ResponseHelper');

var errors = require('../../errors');

// ------------------
// Privates
// ------------------
var _buildCriteria = function(date, gap, featuredRank) {
    return {
        '$and' : [
            {'featuredRank' : featuredRank},
            {'create' : {'$gte' : new Date(date.getTime())}},
            {'create' : {'$lt' : new Date(date.getTime() + gap)}}
        ]
    };
};

var _queryTopOwners = function(req, criteria, callback) {
    Show.aggregate([
        {'$match' : criteria}, 
        {'$group' : {
            '_id' : '$ownerRef',
            'numView' : {'$sum' : '$numView'}
        }}, 
        {'$sort' : {'numView' : -1}}
    ], function(err, results) {
        if (err) {
            callback(errors.genUnkownError(err));
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
                    callback(errors.genUnkownError(err));
                } else {
                    topOwners = topOwners.map(function(object) {
                        return object.ownerRef;
                    });
                    callback(null, {
                        'indexOfCurrentUser' : indexOfCurrentUser,
                        'numOwners' : results.length,
                        'topOwners' : topOwners
                    });
                }
            });
        }
    });
};

var ONE_HOUR = 1 * 3600 * 1000;
var ONE_DAY = 24 * 3600 * 1000;
// ------------------
// Services
// ------------------
var feedingAggregation = module.exports;

feedingAggregation.matchNew = {
    'method' : 'get',
    'func' : [
        function(req, res, next) {
            var date = RequestHelper.parseDate(req.queryString.date);
            var hour, tasks = [];
            for (hour = 0; hour < 24; hour++) {
                // Top owners
                tasks.push((function(hour) {
                    return function(callback) {
                        var criteria = _buildCriteria(
                            new Date(date.getTime() + ONE_HOUR * hour), ONE_HOUR, ShowCode.FEATURED_RANK_NEW);
                        _queryTopOwners(req, criteria, callback);
                    };
                })(hour));
                // Top shows
                tasks.push((function(hour) {
                    return function(callback) {
                        var criteria = _buildCriteria(
                            new Date(date.getTime() + ONE_HOUR * hour), ONE_HOUR, ShowCode.FEATURED_RANK_NEW);
                        Show.find(criteria).limit(3).exec(function(err, shows) {
                            callback(err, {'topShows' : shows});
                        });
                    };
                })(hour));
            }
            async.parallel(tasks, function(err, results) {
                var data = {};
                if (err) {
                    next(errors.genUnkownError(err));
                } else {
                    results.forEach(function(result, index) {
                        var hour = parseInt(index / 2);
                        data[hour] = data[hour] || {};
                        if (index % 2 === 0) {
                            data[hour].topOwners = result.topOwners;
                            data[hour].numOwners = result.numOwners;
                            data[hour].indexOfCurrentUser = result.indexOfCurrentUser;
                        } else {
                            data[hour].topShows = result.topShows;
                        }
                    });
                    ResponseHelper.writeData(res, data);
                    next();
                }
            });
        }
    ]
};

feedingAggregation.featuredTopOwners = {
    'method' : 'get',
    'func' : [
        function(req, res, next) {
            var criteria = _buildCriteria(
                RequestHelper.parseDate(req.queryString.date), ONE_DAY, ShowCode.FEATURED_RANK_TALENT);
                
            _queryTopOwners(req, criteria, function(err, data) {
                if (err) {
                    next(errors.genUnkownError(err));
                } else {
                    ResponseHelper.writeData(res, data);
                    next();
                }
            });
        }
    ]
};

feedingAggregation.matchHotTopOwners = {
    'method' : 'get',
    'func' : [
        function(req, res, next) {
            var criteria = _buildCriteria(
                RequestHelper.parseDate(req.queryString.date), ONE_DAY, ShowCode.FEATURED_RANK_HOT);
                
            _queryTopOwners(req, criteria, function(err, data) {
                if (err) {
                    next(errors.genUnkownError(err));
                } else {
                    ResponseHelper.writeData(res, data);
                    next();
                }
            });
        }
    ]
};
