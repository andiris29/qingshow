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

var ONE_MINUTE = 1 * 60 * 1000;
var ONE_HOUR = 1 * 3600 * 1000;
var ONE_DAY = 24 * 3600 * 1000;
// ------------------
// Privates
// ------------------
var _buildCriteria = function(date, gap) {
    return {
        '$and' : [
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
                numViewOfCurrentUser = -1;
            results.forEach(function(result, index) {
                if (topOwners.length < 8) {
                    topOwners.push({'ownerRef' : result._id});
                }
                if (req.qsCurrentUserId && 
                    req.qsCurrentUserId.toString() === result._id.toString()) {
                    numViewOfCurrentUser = result.numView;
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
                    }).filter(function(ownerRef) {
                        return ownerRef;
                    });
                    callback(null, {
                        'numViewOfCurrentUser' : numViewOfCurrentUser,
                        'numOwners' : results.length,
                        'topOwners' : topOwners
                    });
                }
            });
        }
    });
};

var _queryTopShows = function(criteria, callback) {
    Show.find(criteria).sort({'numView' : -1}).limit(3).exec(function(err, shows) {
        callback(err, {'topShows' : shows});
    });
};


var _cache = {};
setInterval(function() {
    _cache = {};
}, ONE_MINUTE * 5);

var _writeCache = function(category, keyword, cache) {
    _cache[category] = _cache[category] || {};
    _cache[category][keyword] = cache;
};

var _readCache = function(category, keyword, cache) {
    if (_cache[category]) {
        return _cache[category][keyword];
    }
};

// ------------------
// Services
// ------------------
var feedingAggregation = module.exports;

feedingAggregation.latest = {
    'method' : 'get',
    'func' : [
        function(req, res, next) {
            var date = new Date(),
                tasks = [];
            date.setMinutes(0);
            date.setSeconds(0);
            date.setMilliseconds(0);
            for (var offset = 0; offset < 24; offset++) {
                // Top owners
                tasks.push((function(offset) {
                    return function(callback) {
                        var from = new Date(date.getTime() - ONE_HOUR * offset),
                            criteria = _buildCriteria(from, ONE_HOUR),
                            keyword = from.getTime();
                        var cache = _readCache('_queryTopOwners', keyword);
                        if (offset > 0 && cache) {
                            callback(null, cache);
                        } else {
                            _queryTopOwners(req, criteria, function(err, result) {
                                _writeCache('_queryTopOwners', keyword, result);
                                callback(err, result);
                            });
                        }
                    };
                })(offset));
                // Top shows
                tasks.push((function(offset) {
                    return function(callback) {
                        var from = new Date(date.getTime() - ONE_HOUR * offset),
                            criteria = _buildCriteria(from, ONE_HOUR),
                            keyword = from.getTime();
                        var cache = _readCache('_queryTopShows', keyword);
                        if (offset > 0 && cache) {
                            callback(null, cache);
                        } else {
                            _queryTopShows(criteria, function(err, result) {
                                _writeCache('_queryTopShows', keyword, result);
                                callback(err, result);
                            });
                        }
                    };
                })(offset));
            }
            async.parallel(tasks, function(err, results) {
                var data = {};
                if (err) {
                    next(errors.genUnkownError(err));
                } else {
                    results.forEach(function(result, index) {
                        var offset = parseInt(index / 2),
                            x = date.getHours() - offset;
                        data[x] = data[x] || {};
                        if (index % 2 === 0) {
                            data[x].topOwners = result.topOwners;
                            data[x].numOwners = result.numOwners;
                            data[x].numViewOfCurrentUser = result.numViewOfCurrentUser;
                        } else {
                            data[x].topShows = result.topShows;
                        }
                    });
                    ResponseHelper.writeData(res, data);
                    next();
                }
            });
        }
    ]
};
