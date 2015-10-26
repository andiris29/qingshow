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

    if (res.locals && res.locals.clientInfo) {
        var log = _.extend(res.locals.clientInfo, {
            'cost' : Date.now() - res.locals.time,
            'fullpath' : res.locals.fullpath
        }, {
            'qsCurrentUserId' : res.locals.qsCurrentUserId ? res.locals.qsCurrentUserId.toString() : ''
        });
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

ResponseHelper.responseAsMiddleware = function(res, err, data, metadata) {
    res.locals.out = {
        'err' : err,
        'data' : data,
        'metadata' : metadata
    };
};
