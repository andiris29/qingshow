// @formatter:off
define([
    'violet/utils/OOUtil',
    'violet/ui/core/UIComponent',
    'qingshow/services/HTTPService',
    'qingshow/view/show/s09/Show',
    'qingshow/view/show/s09/ShowThumb',
    'qingshow/view/show/s09/ItemThumb'
], function(OOUtil, UIComponent, 
    HTTPService, 
    Show, ShowThumb, ItemThumb) {
// @formatter:on
    var S09ShareShow = function(dom, options) {
        S09ShareShow.superclass.constructor.apply(this, arguments);

        async.parallel([
        function(callback) {
            TemplateService.load('/view/show/s09/S09ShareShow.html', callback);
        },
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
            new Show($('.qs-s09-show', this._dom), {
                'show' : results[1].data.shows[0]
            });
            new ShowThumb($('.qs-s09-thumb-show:eq(0)', this._dom), {
                'show' : results[2].data.shows[0]
            });
            new ShowThumb($('.qs-s09-thumb-show:eq(1)', this._dom), {
                'show' : results[2].data.shows[1]
            });
            new ItemThumb($('.qs-s09-thumb-item:eq(0)', this._dom), {
                'item' : results[3].data.items[0]
            });
            new ItemThumb($('.qs-s09-thumb-item:eq(1)', this._dom), {
                'item' : results[3].data.items[1]
            });
        });
    };
    OOUtil.extend(S09ShareShow, UIComponent);

    return S09ShareShow;
});
