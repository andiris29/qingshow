// @formatter:off
define([
], function() {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var DataService = {};

    DataService.request = function(type, path, requestData, callback) {
        var request = {
            'dataType' : 'json',
            'cache' : false
        };
        if (andrea.env.uriQuery.appServer === 'fake') {
            var suffix = [];
            for (var key in requestData) {
                if (path === '/feeding/chosen' && key === 'type') {
                    suffix.push(key + '' + requestData[key]);
                } else if (path === '/feeding/byTag' && key === 'tags') {
                    suffix.push('tag' + requestData[key][0]);
                }
            }

            suffix = suffix.length ? ('_' + suffix.join('_')) : '';
            $.extend(request, {
                'url' : appConfig.appServer + path + suffix + '.json'
            });
        } else {
            $.extend(request, {
                'url' : appConfig.appServer + path,
                'type' : type,
                'data' : requestData
            });
        }

        $.ajax(request).done(function(responseData) {
            console.log('api: ' + path, requestData, responseData);
            if (callback) {
                callback(responseData.metadata || {}, responseData.data);
            }
        }).fail(function(target, msg, err) {
            if (callback) {
                // TODO Handle network err
                var metadata = {
                    'error' : msg
                };
                callback(metadata);
            }
        }).always(function() {
        });
    };

    DataService.injectBeforeCallback = function(callback, beforeCallback) {
        return function(metadata, data) {
            if (!metadata.error) {
                beforeCallback(metadata, data, require('app/model'));
            }
            if (callback) {
                callback.apply(null, arguments);
            }
        };
    };

    return DataService;
});
