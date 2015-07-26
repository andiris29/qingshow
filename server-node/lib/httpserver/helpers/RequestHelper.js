var mongoose = require('mongoose');
var _ = require('underscore');
var async = require('async');
var qsftp = require('../../runtime/qsftp');
var path = require('path');



var RequestHelper = module.exports;

RequestHelper.getIp = function (req) {
    return req.header('X-Real-IP') || req.connection.remoteAddress;
};

RequestHelper.parse = function (raw, specifiedParsers) {
    var qsParam = {};
    specifiedParsers = specifiedParsers || {};
    var key = null;
    for (key in raw) {
        if (raw.hasOwnProperty(key)) {
            var parser = specifiedParsers[key];
            if (parser) {
                qsParam[key] = parser(raw[key]);
            } else {
                qsParam[key] = raw[key];
            }
        }
    }
    return qsParam;
};

RequestHelper.parsePageInfo = function (raw) {
    var qsParam = RequestHelper.parse(raw, {
        'pageNo' : RequestHelper.parseNumber,
        'pageSize' : RequestHelper.parseNumber
    });
    qsParam.pageNo = qsParam.pageNo || 1;
    qsParam.pageSize = qsParam.pageSize || 10;
    return qsParam;
};

RequestHelper.parseNumber = function (string) {
    return string === undefined ? undefined : parseFloat(string);
};

RequestHelper.parseDate = function (string) {
    if (string !== undefined) {
        var date = new Date(string);
        return date;
    }
};

RequestHelper.parseId = function (string) {
    return string === undefined ? undefined : new mongoose.Types.ObjectId(string);
};

RequestHelper.parseArray = function (raw) {
    if (raw === undefined) {
        return undefined;
    } else if (_.isString(raw)) {
        return raw.split(',');
    } else if (_.isArray(raw)) {
        return raw;
    } else {
        return undefined;
    }
};

RequestHelper.parseIds = function (string) {
    return RequestHelper.parseArray(string).map(function(element) {
        return RequestHelper.parseId(element);
    });
};

RequestHelper.parseFile = function (req, uploadPath, resizeOptions, callback) {
    var formidable = require('formidable');

    var form = new formidable.IncomingForm();
    form.keepExtensions = true;
    form.parse(req, function(err, fields, files) {
        if (err) {
            callback(err);
        }
        var file;
        for (var key in files) {
            file = files[key];
        }

        var savedName = path.basename(file.path);
        var fullPath = path.join(uploadPath, savedName);

        qsftp.uploadWithResize(file.path, savedName, uploadPath, resizeOptions, function (err) {
            file.path = fullPath;
            callback(err, fields, file);
        });
    });
};

