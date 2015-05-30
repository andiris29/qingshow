var _ = require('underscore');
var winston = require('winston');

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
        var performance = Date.now() - res.qsPerformance.start;
        if (performance > 100) {
            winston.warn(new Date().toString() + ': qsPerformance');
            winston.warn(res.qsPerformance.fullpath + ': ' + performance);
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
