var async = require('async'), _ = require('underscore');
var errors = require('../errors');
var RequestHelper = require('./RequestHelper');

var MongoHelper = module.exports;

/**
 *
 * @param {Object} query
 * @param {Object} queryCount
 * @param {Object} pageNo
 * @param {Object} pageSize
 * @param {Object} callback function(err, models, count)
 */
MongoHelper.queryPaging = function(query, queryCount, pageNo, pageSize, callback) {
    async.waterfall([
    function(callback) {
        // Count
        queryCount.count(function(err, count) {
            if (err) {
                callback(errors.genUnkownError(err));
            } else {
                if ((pageNo - 1) * pageSize >= count) {
                    callback(errors.PagingNotExist);
                } else {
                    callback(null, count);
                }
            }
        });
    },
    function(count, callback) {
        // Query
        query.skip((pageNo - 1) * pageSize).limit(pageSize).exec(function(err, models) {
            if (err) {
                callback(errors.genUnkownError(err));
            } else {
                callback(err, models, count);
            }
        });
    }], callback);
};

MongoHelper.aggregatePaging =  function(aggregate, pageNo, pageSize, callback) {
    async.waterfall([
        function(callback) {
            // Count 
            aggregate.exec(function(err, data) {
                if (err) {
                    callback(errors.genUnkownError(err));
                } else {
                    if ((pageNo - 1) * pageSize >= data.length) {
                        callback(errors.PagingNotExist);
                    } else {
                        callback(null, data.length);
                    }
                }
            });
        },
        function(count, callback) {
            // Query
            aggregate.skip((pageNo - 1) * pageSize).limit(pageSize).exec(function(err, models) {
                if (err) {
                    callback(errors.genUnkownError(err));
                } else {
                    callback(err, models, count);
                }
            });
        }], callback);
};

/**
 *
 * @param {Object} query
 * @param {Object} size
 * @param {Object} callback function(err, models)
 */
MongoHelper.queryRandom = function(query, queryCount, size, callback) {
    async.waterfall([
    function(callback) {
        // Count
        queryCount.count(function(err, count) {
            if (err) {
                callback(errors.genUnkownError(err));
            } else {
                callback(null, count);
            }
        });
    },
    function(count, callback) {
        var tasks = [], skipped = [];
        size = Math.min(size, count);
        for (var i = 0; i < size; i++) {
            tasks.push(function(callback) {
                // Generate skip
                var skip;
                while (skip === undefined || skipped.indexOf(skip) !== -1) {
                    skip = _.random(0, count - 1);
                }
                skipped.push(skip);
                // Query
                query.skip(skip).limit(1).exec(function(err, models) {
                    if (err) {
                        callback(errors.genUnkownError(err));
                    } else {
                        callback(err, models[0]);
                    }
                });
            });
        }
        async.series(tasks, callback);
    }], callback);
};

MongoHelper.querySchema = function(Model, qsParam) {
    var criteria = {};
    for (var key in qsParam) {
        var value = qsParam[key];

        if (key === '__context' || key === '__v' || key === 'pageNo' || key === 'pageSize') {
            continue;
        }
        if (!value || value.length == 0) {
            criteria[key] = null;
            continue;
        }
        var column = Model.schema.paths[key];
        if (column == null) {
            continue;
        }
        var type = column.instance;

        var rawValue = value;
        if (type == 'String') {
            rawValue = value;
        } else if (type == 'Number') {
            rawValue = RequestHelper.parseNumber(value);
        } else if (type == 'Date') {
            rawValue = RequestHelper.parseDate(value);
        } else if (type == 'ObjectId') {
            rawValue = RequestHelper.parseId(value);
        } else if (type == 'Mixed') {
            continue;
        } else if (type == 'Array') {
            continue;
        }

        criteria[key] = rawValue;
    }

    return criteria;
};
