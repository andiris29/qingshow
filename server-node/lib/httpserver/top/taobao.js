var async = require('async'), _ = require('underscore');
var request = require('request');

var _appKey = null;
var _appSecret = null;

var _environment = function(value) {
    if (value === 'graceDevelopment') {
        _appKey = '21700680';
        _appSecret = '98831154902e1129c977cf7f8c2e2145';
    } else if (value === 'graceProduction') {
        _appKey = '21613035';
        _appSecret = '0cdf701594faeb88d2dd5c564bbbe5ce';
    } else if (value === 'focosee') {
        _appKey = '21557116';
        _appSecret = '673790b6f46d07a8e7a11702af6ca8d4';
    }
};
_environment('graceDevelopment');

var taobao = module.exports;

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
    request.post({
        'url' : 'http://gw.api.taobao.com/router/rest',
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
            if (response.headers['content-encoding'] === 'gzip') {
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

