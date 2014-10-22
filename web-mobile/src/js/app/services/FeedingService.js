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

    var _request = function(path, data, pageNo, callback) {
        DataService.request('GET', path, $.extend(data || {}, {
            'pageNo' : pageNo,
            'pageSize' : _pageSize
        }), callback);
    };

    FeedingService.choosen = function(pageNo, callback) {
        _request('/feeding/choosen', null, pageNo, callback);
    };

    FeedingService.hot = function(pageNo, callback) {
        _request('/feeding/hot', null, pageNo, callback);
    };

    FeedingService.like = function(pageNo, callback) {
        _request('/feeding/like', null, pageNo, callback);
    };

    FeedingService.recommendation = function(pageNo, callback) {
        _request('/feeding/recommendation', null, pageNo, callback);
    };

    FeedingService.byModel = function(_id, pageNo, callback) {
        _request('/feeding/byModel', {
            '_id' : _id
        }, pageNo, callback);
    };

    FeedingService.byFollow = function(_id, pageNo, callback) {
        _request('/feeding/byFollow', {
            '_id' : _id
        }, pageNo, callback);
    };

    FeedingService.byBrand = function(_id, pageNo, callback) {
        _request('/feeding/byBrand', {
            '_id' : _id
        }, pageNo, callback);
    };

    FeedingService.byTag = function(tags, pageNo, callback) {
        if (_.isString(tags)) {
            tags = [tags];
        }
        _request('/feeding/byTag', {
            'tags' : tags
        }, pageNo, callback);
    };

    return FeedingService;
});
