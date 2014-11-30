var _ = require('underscore');

var ServerError = require('../server-error');

module.exports.generateGeneralCallback = function(res, dataBuilder) {
    return function(err, result) {
        var data = (!err && dataBuilder) ? dataBuilder(result) : result;
        _general(res, err, data);
    };
};

var _general = module.exports.general = function(res, err, data) {
    if (err) {
        if (_.isString(err)) {
            err = new ServerError(ServerError.ServerError, err);
        }
        res.json({
            'metadata' : {
                'error' : err.errorCode,
                'errorInfo' : err
            }
        });
    } else {
        if (data) {
            res.json({
                'data' : data
            });
        } else {
            res.end();
        }
    }
};
