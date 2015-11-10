var errors = require('../../errors');

module.exports = function(req, res, next) {
    if (req.qsCurrentUserId) {
        if (req.injection.qsCurrentUser) {
            if (req.injection.qsCurrentUser.role === 3) {
                next();
            } else {
                next(errors.ERR_PERMISSION_DENIED);
            }
        } else {
            next(errors.genUnkownError('Please injectCurrentUser before validateAdmin.'));
        }
    } else {
        next(errors.ERR_NOT_LOGGED_IN);
    }
};
