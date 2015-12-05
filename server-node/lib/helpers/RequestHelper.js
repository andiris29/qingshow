var mongoose = require('mongoose');
var _ = require('underscore');
var async = require('async');
var qsftp = require('../runtime/').ftp,
    loggers = require('../runtime/loggers');
var path = require('path');
var fs = require('fs');

var RequestHelper = module.exports;

RequestHelper.getIp = function (req) {
    return req.header('X-Real-IP') || req.connection.remoteAddress;
};

RequestHelper.getVersion = function (req) {
    return req.header('qs-version') || 
        (req.queryString ? req.queryString.version : global.qsConfig.system.production.maxSupportedVersion);
};

RequestHelper.getClientInfo = function (req) {
    return {
        'qs-device-model' : req.header('qs-device-model'),
        'qs-device-uid' : req.header('qs-device-uid'),
        'qs-os-type' : req.header('qs-os-type'),
        'qs-type' : req.header('qs-type'),
        'qs-version' : req.header('qs-version'),
        'qs-version-code' : req.header('qs-version-code'),
        'qs-client-ip' : RequestHelper.getIp(req)
    };
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
        var d;
        if (isNaN(string)) {
            // 2015-11-25T11:00:00+08:00
            d = new Date(string);
        } else {
            d = new Date(Number(string));
        }
        return d;
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

RequestHelper.parseNumbers =  function (string) {
    return RequestHelper.parseArray(string).map(function(element) {
        return RequestHelper.parseNumber(element);
    });
};

RequestHelper.parseFile = function (req, uploadPath, savedName, resizeOptions, callback) {
    var formidable = require('formidable');

    var form = new formidable.IncomingForm();
    form.keepExtensions = true;
    
    var logRandom = _.random(100000, 999999);
    var s = new Date().getTime();
    loggers.get('performance-image-upload').info({
        'step' : 'parse',
        'parseFile.logRandom' : logRandom,
        'qsCurrentUserId' : req.qsCurrentUserId ? req.qsCurrentUserId.toString() : ''
    });
    form.parse(req, function(err, fields, files) {
        loggers.get('performance-image-upload').info({
            'step' : 'parse-complete',
            'parseFile.logRandom' : logRandom
        }); 
        if (err) {
            callback(err);
        }
        var file;
        for (var key in files) {
            file = files[key];
        }
        savedName = savedName || '';
        savedName += path.extname(file.path);
        var fullPath = path.join(uploadPath, savedName);
        var oldPath = file.path;
        qsftp.uploadWithResize(file.path, savedName, uploadPath, resizeOptions, function (err) {
            file.path = fullPath;
            callback(err, fields, file);
            var cost = new Date().getTime() - s;
            loggers.get('performance-image-upload').info({
                'step' : 'uploadWithResize-complete',
                'parseFile.logRandom' : logRandom,
                'fullPath' : fullPath,
                'cost' : cost,
                'slow' : cost > 3000,
                'fast' : cost <= 1000,
                'qsCurrentUserId' : req.qsCurrentUserId ? req.qsCurrentUserId.toString() : ''
            });

            try {
                fs.unlink(oldPath, function (){});
            } catch (e) {
            }
        });
    });
};

