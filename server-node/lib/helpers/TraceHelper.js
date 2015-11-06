var _ = require('underscore');

var RequestHelper = require('./RequestHelper');

var TraceHelper = module.exports;

TraceHelper.trace = function(behavior, req, info){
	var logger = require('../runtime/loggers').get(behavior);
	logger.info(_.extend(RequestHelper.getClientInfo(req), info, {
        'qsCurrentUserId' : req.qsCurrentUserId ? req.qsCurrentUserId.toString() : '',
        'mobile' : req.session.mobile ? req.session.mobile : '',
        'nickname' : req.session.nickname ? req.session.nickname : '',
    }));
};
