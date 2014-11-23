// @formatter:off
define([
    'app/services/DataService'
], function(DataService) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var PotentialService = {};

    PotentialService.queryAvailablePItems = function(categories, pageNo, callback) {
        DataService.request('GET', '/potential/queryAvailablePItems', {
            'categories' : categories,
            'pageNo' : pageNo,
            'pageSize' : PotentialService.PITEM_PAGE_SIZE
        }, callback);
    };

    PotentialService.getUnshotPShows = function(_id, callback) {
        DataService.request('GET', '/potential/getUnshotPShows', {
            '_id' : _id
        }, callback);
    };

    PotentialService.collocate = function(_ids, callback) {
        DataService.request('POST', '/potential/collocate', {
            '_ids' : _ids
        }, callback);
    };

    return PotentialService;
});
