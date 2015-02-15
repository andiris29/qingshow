// @formatter:off
define([
    'violet/utils/OOUtil',
    'violet/ui/core/UIComponent',
    'qingshow/services/HTTPService',
    'qingshow/view/ViewFactory'
], function(OOUtil, UIComponent, 
    HTTPService, ViewFactory) {
// @formatter:on
    var S09ShareShow = function(dom, options) {
        S09ShareShow.superclass.constructor.apply(this, arguments);

        async.parallel([
        function(callback) {
            HTTPService.request('/show/query', {
                '_ids' : [options._id]
            }, callback);
        },
        function(callback) {
            HTTPService.request('/feeding/chosen', {
                'pageSize' : 2
            }, callback);
        },
        function(callback) {
            HTTPService.request('/itemFeeding/random', {
                'pageSize' : 2
            }, callback);
        }], function(err, results) {
            ViewFactory.create('/show/s09/Show', $('.qs-s09-show', this._dom).get(0), {
                'show' : results[0].data.shows[0]
            });
            ViewFactory.create('/show/s09/ShowThumb', $('.qs-s09-thumb-show:eq(0)', this._dom).get(0), {
                'show' : results[1].data.shows[0]
            });
            ViewFactory.create('/show/s09/ShowThumb', $('.qs-s09-thumb-show:eq(1)', this._dom).get(0), {
                'show' : results[1].data.shows[1]
            });
            ViewFactory.create('/show/s09/ItemThumb', $('.qs-s09-thumb-item:eq(0)', this._dom).get(0), {
                'show' : results[2].data.items[0]
            });
            ViewFactory.create('/show/s09/ItemThumb', $('.qs-s09-thumb-item:eq(1)', this._dom).get(0), {
                'show' : results[2].data.items[1]
            });
        });
    };
    OOUtil.extend(S09ShareShow, UIComponent);

    return S09ShareShow;
});
