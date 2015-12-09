var errors = require('../../errors');

module.exports = function(req, res, next) {
    if (req.qsCurrentUserId) {
        next();
    } else {
        next(errors.ERR_NOT_LOGGED_IN);
    }
};
