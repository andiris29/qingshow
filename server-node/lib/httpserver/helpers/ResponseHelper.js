var _ = require('underscore');

var ServerError = require('../server-error');

var ResponseHelper = module.exports;

ResponseHelper.response = function(res, err, data, metadata, beforeResponse) {
    var json = {
        'data' : data,
        'metadata' : metadata || {}
    };
    if (err) {
        if (_.isNumber(err)) {
            err = ServerError.fromCode(err);
        } else if ( err instanceof Error) {
            err = ServerError.fromError(err);
        } else if (!( err instanceof ServerError)) {
            err = ServerError.fromDescription(err);
        }
        json.metadata.error = err.errorCode;
        json.metadata.devInfo = err;
    }
    if (beforeResponse) {
        json = beforeResponse(json);
    }

    var performance = Date.now() - res.qsPerformance.start;
    if (performance > 1000) {
        console.log(res.qsPerformance.fullpath + ': ' + performance);
    }
    res.json(json);
};

ResponseHelper.responseAsPaging = function(res, err, data, pageSize, numTotal, beforeResponse) {
    var metadata;
    if (!err) {
        metadata = {
            'numTotal' : numTotal,
            'numPages' : parseInt((numTotal + pageSize - 1) / pageSize)
        };
    }
    ResponseHelper.response(res, err, data, metadata, beforeResponse);
};
