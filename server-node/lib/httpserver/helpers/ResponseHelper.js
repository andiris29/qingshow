var _ = require('underscore');

var ServerError = require('../server-error');

var ResponseHelper = module.exports;

ResponseHelper.generateAsyncCallback = function(res, dataBuilder, metadataBuilder) {
    return function(err, result) {
        var data, metadata;
        if (!err) {
            data = dataBuilder ? dataBuilder.apply(null, arguments) : null;
            metadata = metadataBuilder ? metadataBuilder.apply(null, arguments) : null;
        }
        _general(res, err, data, metadata);
    };
};

ResponseHelper.responseAsPaging = function(res, err, data, pageSize, numTotal, beforeResponse) {
    var json = {
        'data' : data,
        'metadata' : {}
    };
    if (err) {
        json = _appendError(json, err);
    } else {
        json.metadata.numTotal = numTotal;
        json.metadata.numPages = parseInt((numTotal + pageSize - 1) / pageSize);
    }
    if (beforeResponse) {
        json = beforeResponse(json);
    }
    res.json(json);
};

var _appendError = function(json, err) {
    if (err) {
        if (_.isNumber(err)) {
            err = ServerError.fromCode(err);
        } else if (!( err instanceof ServerError)) {
            err = ServerError.fromDescription(err);
        }
        json.metadata = json.metadata || {};
        json.metadata.error = err.errorCode;
        json.metadata.errorInfo = err;
    }
    return json;
};

var _general = ResponseHelper.general = function(res, err, data, metadata) {
    if (err) {
        if (_.isNumber(err)) {
            err = ServerError.fromCode(err);
        } else if (!( err instanceof ServerError)) {
            err = ServerError.fromDescription(err);
        }
        res.json({
            'metadata' : {
                'error' : err.errorCode,
                'errorInfo' : err
            }
        });
    } else {
        if (data || metadata) {
            var json = {};
            if (metadata) {
                json.metadata = metadata;
            }
            if (data) {
                json.data = data;
            }
            res.json(json);
        } else {
            res.end();
        }
    }
};
