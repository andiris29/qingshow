var errors = require('../../errors'),
    People = require('../../dbmodels').People,
    RequestHelper = require('../../helpers/RequestHelper');

var injectModelGenerator = module.exports;

injectModelGenerator.generateInjectByObjectId = function(Model, injectAs) {
    injectModelGenerator.generateInject(Model, injectAs, function(req) {
        var param = req.body || req.queryString || {},
            _id = param._id;
        return {
            '_id' : _id ? RequestHelper.parseId(_id) : null
        };
    });
};

injectModelGenerator.generateInject = function(Model, injectAs, criteriaGenerator) {
    return function(req, res, next) {
        Model.findOne(criteriaGenerator(req), function(err, model) {
            if (err) {
                next(errors.genUnkownError(err));
            } else {
                req.injection[injectAs] = model;
                next();
            }
        });
    };
};
