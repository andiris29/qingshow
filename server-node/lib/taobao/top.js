var async = require('async'), _ = require('underscore');
var request = require('request');

var taobao = module.exports;

// graceDevelopment
var _appKey = '21700680';
var _appSecret = '98831154902e1129c977cf7f8c2e2145';
// TODO
var _session;

var _configs = {};
var _expose = function(api, config) {
    _configs[api] = config;

    api.split('.').reduce(function(previousValue, currentValue, index, array) {
        if (index === 0) {
            return taobao;
        } else if (index !== array.length - 1) {
            return previousValue[currentValue] = {};
        } else {
            return previousValue[currentValue] = function(appParams, callback) {
                _invoke(api, appParams, callback);
            };
        }
    }, null);
};
_expose('taobao.shop.get', {
    'authorize' : false,
    'paging' : false
});

_expose('taobao.item.get', {
    'authorize' : false,
    'paging' : false
});

var _invoke = function(api, appParams, callback) {
    var config = _configs[api];
    // Prepare params
    var sysParams = {
        'method' : api,
        'timestamp' : require('moment')().format('YYYY-MM-DD HH:mm:ss'),
        'format' : 'json',
        'app_key' : _appKey,
        'v' : '2.0',
        'sign_method' : 'md5'
    };
    if (config.authorize) {
        sysParams.session = _session;
    }
    var args = _.extend({}, sysParams, appParams);
    args.sign = _sign(args);
    var data = require('querystring').stringify(args);
    // Post
    var requestUrl = 'http://gw.api.taobao.com/router/rest';
//    requestUrl = 'http://140.205.164.88/router/rest';
    request.post({
        'url' : requestUrl,
        'headers' : {
            'Accept-Language' : 'zh-CN,ja;q=0.8,en-US;q=0.6,en;q=0.4',
            'Accept-Encoding' : 'gzip, deflate',
            'Content-Length' : data.length,
            'Content-Type' : 'application/x-www-form-urlencoded'
        },
        'body' : data,
        'encoding' : null
    }, function(error, response, body) {
        async.waterfall([
        function(callback) {
            // Unzip response
            if (!error && response.headers['content-encoding'] === 'gzip') {
                require('zlib').gunzip(body, function(err, unzipped) {
                    callback(error, response, unzipped.toString());
                });
            } else {
                callback(error, response, body);
            }
        },
        function(response, body, callback) {
            // Handle response
            if (!error && response.statusCode == 200) {
                try {
                    var result = JSON.parse(body);
                    if (result.error_response) {
                        callback(result);
                    } else {
                        callback(null, result);
                    }
                } catch(error) {
                    callback(error);
                }
            } else {
                callback(error);
            }
        }], callback);
    });
};

var _sign = function(params) {
    var s = _appSecret;
    _.each(_.keys(params).sort(), function(key) {
        s += key + params[key];
    });
    s += _appSecret;
    return _hash('md5', s).toUpperCase();
};

var _hash = function(method, s, format) {
    var sum = require('crypto').createHash(method);
    var isBuffer = Buffer.isBuffer(s);
    if (!isBuffer && typeof s === 'object') {
        s = JSON.stringify(sortObject(s));
    }
    sum.update(s, isBuffer ? 'binary' : 'utf8');
    return sum.digest(format || 'hex');
};

