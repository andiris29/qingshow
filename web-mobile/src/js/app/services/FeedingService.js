// @formatter:off
define([
    'app/services/DataService'
], function(DataService) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var FeedingService = {};

    var _request = function(path, data, pageNo, callback) {
        DataService.request('GET', path, $.extend(data || {}, {
            'pageNo' : pageNo,
            'pageSize' : FeedingService.PAGE_SIZE
        }), callback);
    };
    FeedingService.PAGE_SIZE = 10;

    FeedingService.chosenByEditor = function(pageNo, callback) {
        _request('/feeding/chosen', {
            'type' : 0
        }, pageNo, callback);
    };

    FeedingService.chosenByPromotion = function(pageNo, callback) {
        _request('/feeding/chosen', {
            'type' : 1
        }, pageNo, callback);
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

    FeedingService.byBrand = function(_id, pageNo, callback) {
        _request('/feeding/byBrand', {
            '_id' : _id
        }, pageNo, callback);
    };

    FeedingService.tagBag = function(pageNo, callback) {
        _request('/feeding/byTag', {
            'tags' : [1]
        }, pageNo, callback);
    };
    FeedingService.tagAccesories = function(pageNo, callback) {
        _request('/feeding/byTag', {
            'tags' : [3]
        }, pageNo, callback);
    };
    FeedingService.tagShoe = function(pageNo, callback) {
        _request('/feeding/byTag', {
            'tags' : [2]
        }, pageNo, callback);
    };
    FeedingService.studio = function(pageNo, callback) {
        _request('/feeding/studio', null, pageNo, callback);
    };

    return FeedingService;
});
