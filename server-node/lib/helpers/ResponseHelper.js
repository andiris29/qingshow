var _ = require('underscore');
var winston = require('winston');
var apiLogger = winston.loggers.get('api');

var errors = require('../errors');

var ResponseHelper = module.exports;

ResponseHelper.response = function(res, err, data, metadata, beforeEndResponse) {
    var json = {
        'data' : data,
        'metadata' : metadata || {}
    };
    if (err) {
        json.metadata.error = err.errorCode;
        json.metadata.devInfo = err;
    }
    if (beforeEndResponse) {
        json = beforeEndResponse(json);
    }

    if (res.qsPerformance) {
        var log = {
            'ip' : res.qsPerformance.ip,
            'qsCurrentUserId' :res.qsPerformance.qsCurrentUserId, 
            'cost' : Date.now() - res.qsPerformance.start,
            'path' : res.qsPerformance.fullpath
        };
        if (log.cost > 1000) {
            apiLogger.error(log);
        } else if (log.cost > 100) {
            apiLogger.warn(log);
        } else {
            apiLogger.info(log);
        }
    }
    res.json(json);
};

ResponseHelper.responseAsPaging = function(res, err, data, pageSize, numTotal, beforeEndResponse) {
    var metadata;
    if (!err) {
        metadata = {
            'numTotal' : numTotal,
            'numPages' : parseInt((numTotal + pageSize - 1) / pageSize)
        };
    }
    ResponseHelper.response(res, err, data, metadata, beforeEndResponse);
};
