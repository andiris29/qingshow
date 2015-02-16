// @formatter:off
define([
    'violet/utils/OOUtil',
    'violet/utils/UADetector',
    'violet/utils/InteractionUtil',
    'violet/ui/core/UIComponent',
    'qingshow/services/HTTPService',
    'qingshow/view/ViewFactory'
], function(OOUtil, UADetector, InteractionUtil, UIComponent, 
    HTTPService, ViewFactory) {
// @formatter:on
    var S09ShareShow = function(dom, options) {
        S09ShareShow.superclass.constructor.apply(this, arguments);

        this._stageShow = null;

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
            var stageMongoShow = results[0].data.shows[0];
            var thumbMongoShows = results[1].data.shows.filter(function(thumbShow) {
                return thumbShow._id !== stageMongoShow._id;
            });
            ViewFactory.create('/show/s09/Show', this.$('.qs-show').get(0), {
                'show' : stageMongoShow
            }, function(err, module) {
                this._stageShow = module;
            }.bind(this));
            this._createThumbShow(this.$('.qs-thumb-show:eq(0)').get(0), thumbMongoShows[0]);
            this._createThumbShow(this.$('.qs-thumb-show:eq(1)').get(0), thumbMongoShows[1]);
            ViewFactory.create('/show/s09/ItemThumb', this.$('.qs-thumb-item:eq(0)').get(0), {
                'item' : results[2].data.items[0]
            });
            ViewFactory.create('/show/s09/ItemThumb', this.$('.qs-thumb-item:eq(1)').get(0), {
                'item' : results[2].data.items[1]
            });
        }.bind(this));
        // Events for static stuff
        InteractionUtil.onTouchOrClick(this.$('.qs-download'), function(event) {
            if (UADetector.isAndroid()) {
                // TODO Android
            } else {
                window.open('https://itunes.apple.com/us/app/qing-xiu/id946116105');
            }
        });
    };
    OOUtil.extend(S09ShareShow, UIComponent);

    S09ShareShow.prototype._createThumbShow = function(dom, mongoShow) {
        ViewFactory.create('/show/s09/ShowThumb', dom, {
            'show' : mongoShow
        });
    };

    return S09ShareShow;
});
