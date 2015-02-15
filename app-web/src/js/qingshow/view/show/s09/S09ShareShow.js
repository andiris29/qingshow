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
                'pageSize' : 3
            }, callback);
        },
        function(callback) {
            HTTPService.request('/itemFeeding/random', {
                'pageSize' : 2
            }, callback);
        }], function(err, results) {
            var show = results[0].data.shows[0];
            var thumbShows = results[1].data.shows.filter(function(thumbShow) {
                return thumbShow._id !== show._id;
            });
            ViewFactory.create('/show/s09/Show', this.$('.qs-show').get(0), {
                'show' : show
            });
            ViewFactory.create('/show/s09/ShowThumb', this.$('.qs-thumb-show:eq(0)').get(0), {
                'show' : thumbShows[0]
            });
            ViewFactory.create('/show/s09/ShowThumb', this.$('.qs-thumb-show:eq(1)').get(0), {
                'show' : thumbShows[1]
            });
            ViewFactory.create('/show/s09/ItemThumb', this.$('.qs-thumb-item:eq(0)').get(0), {
                'item' : results[2].data.items[0]
            });
            ViewFactory.create('/show/s09/ItemThumb', this.$('.qs-thumb-item:eq(1)').get(0), {
                'item' : results[2].data.items[1]
            });
        });
    };
    OOUtil.extend(S09ShareShow, UIComponent);

    return S09ShareShow;
});
