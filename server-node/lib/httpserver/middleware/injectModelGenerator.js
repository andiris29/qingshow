var errors = require('../../errors'),
    People = require('../../dbmodels').People,
    RequestHelper = require('../../helpers/RequestHelper');

var injectModelGenerator = module.exports;

injectModelGenerator.generateInjectOneByObjectId = function(Model, fromKeyword, toKeyword) {
    toKeyword = toKeyword || fromKeyword;
    return injectModelGenerator.generateInjectOne(Model, toKeyword, function(req) {
        var _id = req.body[fromKeyword] || req.queryString[fromKeyword];
        return {
            '_id' : _id ? RequestHelper.parseId(_id) : null
        };
    });
};

injectModelGenerator.generateInjectOne = function(Model, toKeyword, criteriaGenerator) {
    return function(req, res, next) {
        Model.findOne(criteriaGenerator(req), function(err, model) {
            if (err) {
                next(errors.genUnkownError(err));
            } else {
                req.injection[toKeyword] = model;
                next();
            }
        });
    };
};
