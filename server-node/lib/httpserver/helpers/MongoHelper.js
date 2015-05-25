var async = require('async'), _ = require('underscore');
var ImageUtils = require('../../model/utils/ImageUtils');
var ServerError = require('../server-error');

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
                callback(ServerError.fromDescription(err));
            } else {
                if ((pageNo - 1) * pageSize >= count) {
                    callback(ServerError.fromCode(ServerError.PagingNotExist));
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
                callback(ServerError.fromDescription(err));
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
                    callback(ServerError.fromDescription(err));
                } else {
                    if ((pageNo - 1) * pageSize >= data.length) {
                        callback(ServerError.fromCode(ServerError.PagingNotExist));
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
                    callback(ServerError.fromDescription(err));
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
                callback(ServerError.fromDescription(err));
            } else {
                callback(null, count);
            }
        });
    },
    function(count, callback) {
        var tasks = [], skipped = [], models = [];
        size = Math.min(size, count);
        for (var i = 0; i < size; i++) {
            tasks.push(function(callback) {
                // Generate skip
                var skip;
                while (skip === undefined || skipped.indexOf(skip) !== -1) {
                    skip = _.random(0, count - 1);
                }
                // Query
                query.skip(skip).limit(1).exec(function(err, models) {
                    if (err) {
                        callback(ServerError.fromDescription(err));
                    } else {
                        callback(err, models[0]);
                    }
                });
            });
        }
        async.parallel(tasks, callback);
    }], callback);
};

MongoHelper.updateCoverMetaData = function(models, callback) {
    // Parse cover
    var tasks = [];
    models.forEach(function(model) {
        tasks.push(function(callback) {
            // Update each model
            async.parallel([
            function(callback) {
                ImageUtils.createOrUpdateMetadata(model, model.cover, 'coverMetadata', callback);
            },
            function(callback) {
                ImageUtils.createOrUpdateMetadata(model, (model.images && model.images[0]) ? model.images[0].url : undefined, 'imageMetadata', callback);
            },
            function(callback) {
                ImageUtils.createOrUpdateMetadata(model, model.horizontalCover, 'horizontalCoverMetadata', callback);
            },
            function (callback) {
                ImageUtils.createOrUpdateMetadata(model, model.poster, 'posterMetadata', callback);
            }], function(err, results) {
                // Ignore error
                callback();
            });
        });
    });
    async.parallel(tasks, callback);
};

