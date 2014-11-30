var People = require('../../model/peoples');
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
        // req.qsCurrentUserId = mongoose.mongo.BSONPure.ObjectID(req.session.userId);

        var loginDate = req.session.loginDate;
        People.findOne({
            "_id" : userID
        }).select('userInfo.passwordUpdatedDate').exec(function(err, people) {
            if (err) {
                next(err);
            } else {
                if (!people || !people.userInfo) {
                    // User not found
                    next(new ServerError(ServerError.SessionExpired));
                    return;
                }

                if (!people.userInfo.passwordUpdatedDate) {
                    people.userInfo.passwordUpdatedDate = loginDate;
                }
                if (loginDate < people.userInfo.passwordUpdatedDate) {
                    next(new ServerError(ServerError.SessionExpired));
                } else {
                    People.findOne({
                        "_id" : userID
                    }, function(err, people) {
                        req.currentUser = people;
                        next();
                    });
                }
            }
        });
    };
    return handleValidate;
}

module.exports = validate;
