var mongoose = require('mongoose');

var RequestHelper = module.exports;

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
