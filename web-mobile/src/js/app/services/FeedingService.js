// @formatter:off
define([
    'app/services/DataService'
], function(DataService) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var FeedingService = {};

    var _pageSize = 10;
    FeedingService.choosen = function(pageNo, callback) {
        DataService.request('/feeding/choosen', {
            'pageNo' : pageNo,
            'pageSize' : _pageSize
        }, callback);
    };

    return FeedingService;
});
