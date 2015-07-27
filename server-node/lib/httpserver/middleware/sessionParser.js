var mongoose = require('mongoose');

var _parser = function(req, res, next) {
    if (req.session.userId) {
        req.qsCurrentUserId = new mongoose.Types.ObjectId(req.session.userId);
    }
    next();
};

module.exports = _parser;
