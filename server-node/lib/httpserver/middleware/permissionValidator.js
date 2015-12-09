var async = require('async');
var _ = require('underscore');

var errors = require('../../errors');
var RequestHelper = require('../../helpers/RequestHelper');
var People = require('../../dbmodels').People;
var _validatorsMap = {};

var _init = function(services) {
    services.forEach(function(service) {
        var module = service.module,
            path = service.path;
        for (var id in module) {
            var validators = _globalValidators.concat(module[id].permissionValidators || []);
            if (validators) {
                _validatorsMap['/services/' + path + '/' + id] = validators;
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
                next(err);
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
            callback(errors.ERR_NOT_LOGGED_IN);
        }
    },
    'roleUserValidator' : function(req, res, callback) {
        People.findOne({
            '_id' : req.qsCurrentUserId
        }).exec(function(err, people){
            if (people) {
                if (people.role === 0) {
                    callback(errors.ERR_NOT_LOGGED_IN);
                }else {
                    callback(null);
                }
            }else {
                callback(errors.ERR_NOT_LOGGED_IN);
            }
        });
    }
};

var _globalValidators = [
];

module.exports = _init;
