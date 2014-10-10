// @formatter:off
define([
], function() {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var DataService = {};

    DataService.request = function(path, requestData, callback) {
        var request = {
            'url' : appConfig.dataServerRoot + path,
            'dataType' : 'json',
            'cache' : !andrea.env.debug
        };
        if (andrea.env.debug) {
            $.extend(request, {
                'url' : request.url + '.json',
            });
        } else {
            $.extend(request, {
                'type' : 'POST',
                'data' : requestData
            });
        }

        $.ajax(request).done(function(responseData) {
            console.log('api: ' + path, requestData, responseData);
            callback(null, responseData);
        }).fail(function(target, msg, err) {
            callback(msg);
        }).always(function() {
        });
    };

    return DataService;
});