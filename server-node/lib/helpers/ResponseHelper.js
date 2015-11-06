var _ = require('underscore');
var winston = require('winston');

var logger = require('../runtime').loggers.get('api');

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
            'mobile' : res.qsPerformance.mobile,
            'nickname' : res.qsPerformance.nickname,
            'cost' : Date.now() - res.qsPerformance.start,
            'path' : res.qsPerformance.fullpath
        };
        if (log.cost > 1000) {
            logger.error(log);
        } else if (log.cost > 100) {
            logger.warn(log);
        } else {
            logger.info(log);
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
