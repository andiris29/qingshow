var mongoose = require('mongoose');

var RequestHelper = module.exports;

RequestHelper.parse = function(qsParam, raw, specifiedParsers) {
    qsParam = qsParam || {};
    for (var key in raw) {
        var parser = specifiedParsers[key];
        if (parser) {
            qsParam[key] = parser(raw[key]);
        } else {
            qsParam[key] = raw[key];
        }
    }
    return qsParam;
};

RequestHelper.parseFloat = function(string) {
    return parseFloat(string);
};

RequestHelper.parseDate = function(string) {
    return new Date(string);
};

RequestHelper.parseId = function(string) {
    return mongoose.mongo.BSONPure.ObjectID(string);
};

RequestHelper.parseArray = function(string) {
    return (string || '').split(',');
};

RequestHelper.parseIds = function(string) {
    return RequestHelper.parseArray(string).map(function(element) {
        return RequestHelper.parseId(element);
    });
};
