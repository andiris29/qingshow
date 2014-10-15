var ServicesUtil = require('../servicesUtil');


function error_handler(err, req, res, next) {
    if (!err) {
        return next();
    }
    ServicesUtil.responseError(res, err);
//    next(err);
}

module.exports = error_handler;