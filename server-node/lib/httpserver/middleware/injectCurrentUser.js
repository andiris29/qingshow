var errors = require('../../errors'),
    People = require('../../dbmodels').People;

module.exports = function(req, res, next) {
    if (req.qsCurrentUserId) {
        People.findOne({
            '_id' : req.qsCurrentUserId
        }, function(err, people) {
            if (err) {
                next(errors.genUnkownError(err));
            } else {
                if (!people) {
                    next(errors.ERR_NOT_LOGGED_IN);
                } else {
                    req.injection.qsCurrentUser = people;
                    next();
                }
            }
        });
    } else {
        next(errors.ERR_NOT_LOGGED_IN);
    }
};
