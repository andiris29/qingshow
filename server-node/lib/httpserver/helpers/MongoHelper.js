var async = require('async');
var ImageUtils = require('../../model/utils/ImageUtils');

var ServerError = require('../server-error');

var MongoHelper = module.exports;
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
            }], function(err, results) {
                // Ignore error
                callback();
            });
        });
    });
    async.parallel(tasks, callback);
};

