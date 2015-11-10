var errors = require('../../errors'),
    People = require('../../dbmodels').People,
    RequestHelper = require('../../helpers/RequestHelper');

module.exports.generate = function(Model, name) {
    return function(req, res, next) {
        var param = req.body || req.queryString || {},
            _id = param._id;
        if (!_id) {
            req.injection[name] = null;
            next();
        } else {
            _id = RequestHelper.parseId(_id);
            Model.findOne({
                '_id' : _id
            }, function(err, model) {
                if (err) {
                    next(errors.genUnkownError(err));
                } else {
                    req.injection[name] = model;
                    next();
                }
            });
        }
    };
};
