var async = require('async');

var ServerError = require('../server-error');

module.exports.queryPaging = function(query, queryCount, pageNo, pageSize, callback) {
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
