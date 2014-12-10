var mongoose = require('mongoose');

var _parser = function(req, res, next) {
    if (req.session.userId) {
        req.qsCurrentUserId = mongoose.mongo.BSONPure.ObjectID(req.session.userId);
    }
    next();
};

module.exports = _parser;
