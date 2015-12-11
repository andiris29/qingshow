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
        if (!err.errorCode) {
            err = errors.genUnkownError(err);
        }
        json.metadata.error = err.errorCode;
        json.metadata.devInfo = {
            'err' : err.description || (err.toString ? err.toString() : err),
            'stack' : err.stack
        };
    }
    if (beforeEndResponse) {
        json = beforeEndResponse(json);
    }

    if (res.locals && res.locals.api) {
        var log = _.extend(res.locals.api, {
            'cost' : Date.now() - res.locals.api.timestamp,
            'res' : {
                'metadata' : json.metadata
            }
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

ResponseHelper.write = function(res, metadata, data) {
    ResponseHelper.writeMetadata(res, metadata);
    ResponseHelper.writeData(res, data);
};

ResponseHelper.writeMetadata = function(res, metadata) {
    res.locals.out = res.locals.out || {};
    res.locals.out.metadata = metadata;
};

ResponseHelper.writeData = function(res, data) {
    res.locals.out = res.locals.out || {};
    res.locals.out.data = data;
};
