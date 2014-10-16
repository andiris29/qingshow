// @formatter:off
define([
    'app/services/DataService'
], function(DataService) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var FeedingService = {};

    FeedingService.choosen = function(pageNo, pageSize, callback) {
        DataService.request('/feeding/choosen', {
            'pageNo' : pageNo,
            'pageSize' : pageSize
        }, callback);
    };

    return FeedingService;
});
