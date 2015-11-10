var errors = require('../../errors');

module.exports = function(req, res, next) {
    if (req.qsCurrentUserId) {
        if (req.qsCurrentUser) {
            if (req.qsCurrentUser.role === 3) {
                next(errors.ERR_PERMISSION_DENIED);
            } else {
                next();
            }
        } else {
            next(errors.genUnkownError('Please injectCurrentUser before validateAdmin.'));
        }
    } else {
        next(errors.ERR_NOT_LOGGED_IN);
    }
};
