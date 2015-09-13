var _ = require('underscore');
var winston = require('winston');
var performanceLogger = winston.loggers.get('performance');

var ServerError = require('../server-error');

var ResponseHelper = module.exports;

ResponseHelper.response = function(res, err, data, metadata, beforeEndResponse) {
    var json = {
        'data' : data,
        'metadata' : metadata || {}
    };
    if (err) {
        if (!( err instanceof ServerError)) {
            if (_.isNumber(err)) {
                err = ServerError.fromCode(err);
            } else if ( err instanceof Error) {
                err = ServerError.fromError(err);
            } else {
                err = ServerError.fromDescription(err);
            }
        }
        json.metadata.error = err.errorCode;
        json.metadata.devInfo = err;
    }
    if (beforeEndResponse) {
        json = beforeEndResponse(json);
    }

    if (res.qsPerformance) {
        var log = {
            'ip' : res.qsPerformance.ip,
            'cost' : Date.now() - res.qsPerformance.start,
            'path' : res.qsPerformance.fullpath
        };
        if (log.cost > 1000) {
            performanceLogger.error(log);
        } else if (log.cost > 100) {
            performanceLogger.warn(log);
        } else {
            performanceLogger.info(log);
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
