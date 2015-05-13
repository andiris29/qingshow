var mongoose = require('mongoose');

var RequestHelper = module.exports;

RequestHelper.parse = function(raw, specifiedParsers) {
    var qsParam = {};
    specifiedParsers = specifiedParsers || {};
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

RequestHelper.parsePageInfo = function(raw) {
    var qsParam = RequestHelper.parse(raw, {
        'pageNo' : RequestHelper.parseNumber,
        'pageSize' : RequestHelper.parseNumber
    });
    qsParam.pageNo = qsParam.pageNo || 1;
    qsParam.pageSize = qsParam.pageSize || 10;
    return qsParam;
};

RequestHelper.parseNumber = function(string) {
    return string === undefined ? undefined : parseFloat(string);
};

RequestHelper.parseDate = function(string) {
    if (string !== undefined) {
        var date = new Date(string);
        date.setMinutes(date.getMinutes() - date.getTimezoneOffset());
        return date;
    }
};

RequestHelper.parseId = function(string) {
    return string === undefined ? undefined : new mongoose.Types.ObjectId(string);
};

RequestHelper.parseArray = function(string) {
    return string === undefined ? undefined : string.split(',');
};

RequestHelper.parseIds = function(string) {
    return RequestHelper.parseArray(string).map(function(element) {
        return RequestHelper.parseId(element);
    });
};
