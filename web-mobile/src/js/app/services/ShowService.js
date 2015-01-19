// @formatter:off
define([
    'app/services/DataService'
], function(DataService) {
// @formatter:on
    /**
     */
    var ShowService = {};

    ShowService.query = function(_ids, callback) {
        DataService.request('GET', '/show/query', {
            '_ids' : _ids
        }, callback);
    };

    return ShowService;
});
