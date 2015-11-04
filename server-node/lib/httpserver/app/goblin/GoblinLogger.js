var _ = require('underscore');

var logger = require('../../../runtime/loggers').get('goblin');

var RequestHelper = require('../../../helpers/RequestHelper');

var GoblinLogger = module.exports;

GoblinLogger.unsupported = function(item) {
    _step('unsupported', item);
};

GoblinLogger.register = function(item) {
    _step('register', item);
};

GoblinLogger.assign = function(item, req) {
    _step('assign', item, {
        'slaveIp' : RequestHelper.getIp(req)
    });
};

GoblinLogger.complete = function(item, req) {
    _step('complete', item, {
        'slaveIp' : RequestHelper.getIp(req)
    });
};

GoblinLogger.delist = function(item, req) {
    _step('delist', item, {
        'slaveIp' : RequestHelper.getIp(req)
    });
};

GoblinLogger.failed = function(item, req, reason) {
    _step('failed', item, {
        'reason' : reason,
        'slaveIp' : RequestHelper.getIp(req)
    });
};

GoblinLogger.error = function(item, req, err) {
    _step('error', item, {
        'err' : err,
        'slaveIp' : RequestHelper.getIp(req)
    });
};

GoblinLogger.timeout = function(itemId) {
    logger.info({
        'step' : 'timeout',
        '_itemId' : itemId
    });
};

var _step = function(result, item, info) {
    info = info || {};
    
    logger.info(_.extend({
        'step' : result,
        '_itemId' : item._id.toString(),
        'source' : item.source
    }, info));
};
