// @formatter:off
define([
], function() {
// @formatter:on
    var AppService = function() {
    };

    AppService.prototype.request = function(path, method, data, callback) {
        // Handle optional parameters
        if (arguments.length === 2 && arguments[1] instanceof Function) {
            callback = data;
            data = null;
        }
        data = data || {};
        data.version = window.appConfig.VERSION;
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
            'url' : window.appConfig.appServiceRoot + path,
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

    return new AppService();
});
