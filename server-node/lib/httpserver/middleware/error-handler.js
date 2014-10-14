var ServicesUtil = require('../../util/servicesUtil');


function error_handler(err, req, res, next) {
    ServicesUtil.responseError(res, err);
//    next(err);
}

module.exports = error_handler;