// @formatter:off
define([
    'qingshow/core/VERSION'
], function(VERSION) {
// @formatter:on
    var HTTPService = {};

    var _manifests = (function() {
        var result = {};
        var add = function(path, method) {
            result[path] = {
                'method' : method
            };
        };
        add('/show/query', 'get');
        add('/feeding/chosen', 'get');
        add('/itemFeeding/random', 'get');
        return result;
    })();

    HTTPService.request = function(path, data, callback) {
        // Handle optional parameters
        if (arguments.length === 2 && arguments[1] instanceof Function) {
            callback = data;
            data = null;
        }
        data = data || {};
        data.version = VERSION;
        // Transform data to requestable
        for (var key in data) {
            if (data[key] instanceof Array) {
                data[key] = data[key].join(',');
            }
        }
        // Build settings
        var manifest = _manifests[path];
        var settings = {
            'url' : window.appConfig.appServer + path,
            'type' : manifest.method,
            'data' : data,
            'dataType' : 'json',
            'cache' : false,
            'xhrFields' : {
                'withCredentials' : true
            }
        };

        $.ajax(settings).done(function(json) {
            console.log('api done: ' + path, data, json);
            if (callback) {
                callback(null, json);
            }
        }).fail(function(target, msg, err) {
            console.log('api fail: ' + path, msg, err);
            if (callback) {
                callback(err);
            }
        }).always(function() {
        });
    };

    return HTTPService;
});
