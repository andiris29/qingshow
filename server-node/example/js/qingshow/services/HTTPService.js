// @formatter:off
define([
    'qingshow/core/VERSION'
], function(VERSION) {
// @formatter:on
    var HTTPService = {};

    HTTPService.get = function(path, data, callback) {
        var args = Array.prototype.slice.call(arguments);
        // Transform data to requestable
        for (var key in data) {
            if (data[key] instanceof Array) {
                data[key] = data[key].join(',');
            }
        }
        args.splice(1, 0, 'get');
        HTTPService.request.apply(null, args);
    };

    HTTPService.post = function(path, data, callback) {
        var args = Array.prototype.slice.call(arguments);
        args.splice(1, 0, 'post');
        HTTPService.request.apply(null, args);
    };

    HTTPService.request = function(path, method, data, callback) {
        // Handle optional parameters
        if (arguments.length === 3 && arguments[2] instanceof Function) {
            callback = data;
            data = null;
        }
        data = data || {};
        data.version = VERSION;
        // Build settings
        var settings = {
            'url' : window.appConfig.appServer + path,
            'type' : method,
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
