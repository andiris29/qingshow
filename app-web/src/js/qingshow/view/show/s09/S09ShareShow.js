// @formatter:off
define([
    'violet/utils/OOUtil',
    'violet/utils/UADetector',
    'violet/utils/InteractionUtil',
    'violet/ui/core/UIComponent',
    'violet/ui/geometric/Rectangle',
    'qingshow/services/HTTPService',
    'qingshow/view/ViewFactory'
], function(OOUtil, UADetector, InteractionUtil, UIComponent, Rectangle,
    HTTPService, ViewFactory) {
// @formatter:on
    var S09ShareShow = function(dom, options) {
        S09ShareShow.superclass.constructor.apply(this, arguments);

        this._stageShow = null;
        this._animating = false;

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
            this._createStageShow(stageMongoShow);
            this._createThumbShow(this.$('.qs-thumb-show:eq(0)').get(0), thumbMongoShows[0]);
            this._createThumbShow(this.$('.qs-thumb-show:eq(1)').get(0), thumbMongoShows[1]);
            ViewFactory.create('/show/s09/ThumbItem', this.$('.qs-thumb-item:eq(0)').get(0), {
                'item' : results[2].data.items[0]
            });
            ViewFactory.create('/show/s09/ThumbItem', this.$('.qs-thumb-item:eq(1)').get(0), {
                'item' : results[2].data.items[1]
            });
        }.bind(this));
        // Events for static stuff
        InteractionUtil.onTouchOrClick(this.$('.qs-download'), function(event) {
            if (window.WeixinJSBridge || UADetector.isAndroid()) {
                window.open('http://a.app.qq.com/o/simple.jsp?pkgname=com.focosee.qingshow');
            } else {
                window.open('https://itunes.apple.com/us/app/qing-xiu/id946116105?ls=1&mt=8');
            }
        });
    };
    OOUtil.extend(S09ShareShow, UIComponent);

    S09ShareShow.prototype._createStageShow = function(mongoShow) {
        ViewFactory.create('/show/s09/Show', this.$('.qs-show').get(0), {
            'show' : mongoShow
        }, function(err, module) {
            this._stageShow = module;
        }.bind(this));
    };

    S09ShareShow.prototype._createThumbShow = function(dom, mongoShow) {
        ViewFactory.create('/show/s09/ThumbShow', dom, {
            'show' : mongoShow
        }, function(err, thumbShow) {
            thumbShow.on('requestOnStage', function(event) {
                // Swap
                this._animate(thumbShow, function() {
                    var mongoShow = thumbShow.mongoShow();
                    thumbShow.mongoShow(this._stageShow.mongoShow());
                    this._stageShow.mongoShow(mongoShow);
                }.bind(this));
            }.bind(this));
        }.bind(this));
    };

    S09ShareShow.prototype._animate = function(thumbShow, callback) {
        if (this._animating) {
            return;
        }
        this._animating = true;

        var from = Rectangle.parseDOM(thumbShow.dom());
        var to = Rectangle.parseDOM(this._stageShow.dom());

        var animator$ = $(document.createElement('div')).appendTo(document.body);
        animator$.addClass('s09-onstage-animator').css({
            'background-image' : 'url(' + thumbShow.mongoShow().posters[0] + ')',
            'width' : from.width() + 'px',
            'height' : from.height() + 'px',
            'left' : from.left() + 'px',
            'top' : from.top() + 'px'
        });

        animator$.animate({
            'width' : to.width() + 'px',
            'height' : to.height() + 'px',
            'left' : to.left() + 'px',
            'top' : to.top() + 'px'
        }, 300, function() {
            callback();

            _.delay( function() {
                this._animating = false;
                animator$.remove();
            }.bind(this), 300);
        }.bind(this));
    };
    return S09ShareShow;
});
