// @formatter:off
define([
    'app/services/DataService'
], function(DataService) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var QueryService = {};

    QueryService.queryShow = function(_id, callback) {
        _queryShows([_id], callback);
    };
    QueryService.queryShow = function(_ids, callback) {
        _queryShows(_ids, callback);
    };
    var _queryShows = function(_ids, callback) {
        DataService.request('GET', '/query/shows', {
            '_ids' : _ids
        }, function(metadata, data) {
            if (!metadata.error) {
                var model = require('app/model');
                data.shows.forEach(function(show, index) {
                    model.cacheShow(show._id, show);
                });
            }
            callback.apply(null, arguments);
        });
    };
    return QueryService;
});
