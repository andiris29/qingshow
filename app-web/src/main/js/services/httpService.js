// @formatter:off
define([
], function() {
// @formatter:on
    var httpService = {};

    var _root = null,
        _version = null;

    httpService.config = function(value) {
        _root = value.root;
        _version = value.version;
    };

    httpService.request = function(path, method, data, callback) {
        // Handle optional parameters
        if (arguments.length === 2 && arguments[1] instanceof Function) {
            callback = data;
            data = null;
        }
        data = data || {};
        // Transform data to requestable
        if (method === 'get') {
            for (var key in data) {
                if (data[key] instanceof Array) {
                    data[key] = data[key].join(',');
                }
            }
        }
        // Build settings
        var settings = {
            'url' : _root + path + '?version=' + _version,
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
                callback(null, json.metadata, json.data);
            }
        }).fail(function(target, msg, err) {
            console.log('api fail: ' + path, msg, err);
            if (callback) {
                callback(err || 'API fail.');
            }
        }).always(function() {
        });
    };

    return httpService;
});
