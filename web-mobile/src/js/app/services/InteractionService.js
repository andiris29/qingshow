// @formatter:off
define([
    'app/services/DataService'
], function(DataService) {
// @formatter:on
    /**
     *
     */
    var InteractionService = {};

    InteractionService.collocate = function(_ids, callback) {
        DataService.request('POST', '/interaction/collocate', {
            '_ids' : _ids
        }, callback);
    };

    return InteractionService;
});
