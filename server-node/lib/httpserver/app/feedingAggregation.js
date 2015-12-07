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

var ONE_HOUR = 1 * 3600 * 1000;
var ONE_DAY = 24 * 3600 * 1000;
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
                        var criteria = _buildCriteria(
                            new Date(date.getTime() - ONE_HOUR * offset), ONE_HOUR);
                        _queryTopOwners(req, criteria, callback);
                    };
                })(offset));
                // Top shows
                tasks.push((function(offset) {
                    return function(callback) {
                        var criteria = _buildCriteria(
                            new Date(date.getTime() - ONE_HOUR * offset), ONE_HOUR);
                        Show.find(criteria).sort({'numView' : -1}).limit(3).exec(function(err, shows) {
                            callback(err, {'topShows' : shows});
                        });
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
