var errors = require('../../errors'),
    People = require('../../dbmodels').People;

module.exports = function parser(req, res, next) {
    if (req.qsCurrentUserId) {
        People.findOne({
            '_id' : req.qsCurrentUserId
        }, function(err, people) {
            if (err) {
                next(errors.genUnkownError(err));
            } else {
                if (!people) {
                    next(errors.NeedLogin);
                } else {
                    req.qsCurrentUser = people;
                    next();
                }
            }
        });
    } else {
        next(errors.NeedLogin);
    }
};
