// @formatter:off
define([
    'app/services/DataService'
], function(DataService) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var QueryService = {};

    QueryService.show = function(_id, callback) {
        _shows([_id], callback);
    };

    QueryService.shows = function(_ids, callback) {
        _shows(_ids, callback);
    };

    QueryService.PITEM_PAGE_SIZE = 20;

    var _shows = function(_ids, callback) {
        DataService.request('GET', '/query/shows', {
            '_ids' : _ids
        }, DataService.injectBeforeCallback(callback, function(metadata, data, model) {
            data.shows.forEach(function(show, index) {
                model.cacheShow(show._id, show);
            });
        }));
    };
    return QueryService;
});
