var mongoose = require('mongoose');

var ServerError = require('../server-error');

function validate(servicesNames) {
    var validatePaths = [];
    servicesNames.forEach(function(path) {
        var module = require('../services/' + path);
        for (var id in module) {
            var needLogin = module[id].needLogin === true;
            if (needLogin) {
                var servicePath = '/services/' + path + '/' + id;
                validatePaths.push(servicePath);
            }
        }
    });

    var handleValidate = function(req, res, next) {
        var needLogin = validatePaths.indexOf(req.path) !== -1;

        // No session
        var userID = req.session.userId;
        if (!userID) {
            if (needLogin) {
                next(new ServerError(ServerError.NeedLogin));
            } else {
                next();
            }
            return;
        }
        req.qsCurrentUserId = mongoose.mongo.BSONPure.ObjectID(req.session.userId);
        next();
    };
    return handleValidate;
}

module.exports = validate;
