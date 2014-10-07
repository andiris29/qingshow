// @formatter:off
define([
], function() {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var DataService = {};

    DataService.request = function(path, requestData, callback) {
        var request;
        if (andrea.env.debug) {
            request = {
                'dataType' : 'json',
                'url' : '../deps-fake/data' + path + '.json'
            };
        } else {
            request = {
                'url' : appConfig.server.url + path,
                'type' : 'POST',
                'dataType' : 'json',
                'data' : requestData
            };
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
