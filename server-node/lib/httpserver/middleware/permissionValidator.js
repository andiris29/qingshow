var async = require('async');
var _ = require('underscore');

var ServerError = require('../server-error');
var RequestHelper = require('../../helpers/RequestHelper');
var People = require('../../models').People;
var _validatorsMap = {};

var _init = function(services) {
    services.forEach(function(service) {
        var module = service.module,
            path = service.path;
        for (var id in module) {
            var validators = _globalValidators.concat(module[id].permissionValidators || []);
            if (validators) {
                _validatorsMap['/app/' + path + '/' + id] = validators;
            }
        }
    });
    return _validate;
};

var _validate = function(req, res, next) {
    var validators = _validatorsMap[req.path];
    if (validators) {
        var tasks = [];
        validators.forEach(function(validator) {
            tasks.push(function(callback) {
                if (_.isString(validator)) {
                    validator = _builtInValidators[validator];
                }
                //TODO @Hashmap
                if (validator) {
                    validator(req, res, callback);
                } else {
                    callback();
                }

            });
        });
        async.series(tasks, function(err) {
            if (err) {
                next(new ServerError(err));
            } else {
                next();
            }
        });
    } else {
        next();
    }
};

var _builtInValidators = {
    'loginValidator' : function(req, res, callback) {
        if (req.qsCurrentUserId) {
            callback(null);
        } else {
            callback(ServerError.NeedLogin);
        }
    }
};

var _globalValidators = [
    _versionValidator
];

var _versionValidator = function(req, res, callback) {
    var minSupportedVersion = 2.0;
    if (req.body.version === null && req.queryString.version === null) {
        callback(null);
        return;
    }

    var version = 0;
    if (req.body.version !== null) {
        version = RequestHelper.parseNumber(req.body.version);
    } else {
        version = RequestHelper.parseNumber(req.queryString.version);
    }
    if (version < minSupportedVersion) {
        callback(ServerError.UnsupportVersion);
    } else {
        callback(null);
    }
};

module.exports = _init;
