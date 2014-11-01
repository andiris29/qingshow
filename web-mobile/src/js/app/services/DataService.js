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
            'dataType' : 'json'
        };
        if (andrea.env.fake) {
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
                'url' : appConfig.fake.dataServerRoot + path + suffix + '.json',
                'cache' : !andrea.env.nocache
            });
        } else {
            $.extend(request, {
                'url' : appConfig.dataServerRoot + path,
                'type' : type,
                'data' : requestData
            });
        }

        $.ajax(request).done(function(responseData) {
            console.log('api: ' + path, requestData, responseData);
            callback(responseData.metadata, responseData.data);
        }).fail(function(target, msg, err) {
            callback(msg);
        }).always(function() {
        });
    };

    return DataService;
});
