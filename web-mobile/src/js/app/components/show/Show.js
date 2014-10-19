// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/services/DataService',
    'app/utils/CodeUtils',
    'app/utils/RenderUtils'
], function(UIComponent, TemplateManager, DataService, CodeUtils, RenderUtils) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var Show = function(dom, data) {
        Show.superclass.constructor.apply(this, arguments);
        this._show = data;

        async.parallel([ function(callback) {
            // Load template
            TemplateManager.load('show/show.html', function(err, content$) {
                this._dom$.append(content$);
                callback(null);
            }.bind(this));
        }.bind(this)], function(err, results) {
            this._render();
        }.bind(this));

    };
    andrea.oo.extend(Show, UIComponent);

    Show.prototype.destroy = function() {
        Show.superclass.destroy.apply(this, arguments);
        $('.qsSlickVideoPosters', this._dom$).unslick();
        $('.qsSlickItemCovers', this._dom$).unslick();
    };

    Show.prototype._render = function() {
        Show.superclass._render.apply(this, arguments);

        var show = this._show;
        // Video
        var video$ = $('.qsVideo', this._dom$);
        $('<source/>').attr({
            'type' : 'video/mp4',
            'src' : RenderUtils.videoPathToURL(show.video)
        }).appendTo(video$);
        var vjs = videojs(video$.get(0));

        var videoPostersContainer$ = $('.qsSlickVideoPosters', this._dom$), slickItemTplt$;
        show.posters.forEach(function(poster, index) {
            var slickItem$;
            if (index === 0) {
                slickItem$ = slickItemTplt$ = $('.clone', videoPostersContainer$);
            } else {
                slickItem$ = slickItemTplt$.clone().appendTo(videoPostersContainer$);
            }
            $('.qsItemCover', slickItem$).css('background-image', RenderUtils.videoPathToBackground(poster));
        });
        videoPostersContainer$.slick({
            'dots' : true,
            'arrows' : false,
            'slidesToShow' : 1,
            'slidesToScroll' : 1
        });

        var play$ = $('.qsVideoPlay', this._dom$);
        vjs.on('play', function() {
            videoPostersContainer$.hide().off();
            play$.hide();
        });
        vjs.on('pause', function() {
            play$.show();
        });
        vjs.on('click', function(event) {
            if (vjs.paused()) {
                vjs.play();
            } else {
                vjs.pause();
            }
        });
        videoPostersContainer$.on('click', vjs.play.bind(vjs));
        // Model
        $('.qsPortrait', this._dom$).css('background-image', RenderUtils.imagePathToBackground(show.modelRef.portrait));
        $('.qsName', this._dom$).text(show.modelRef.name);
        $('.qsAge', this._dom$).text(RenderUtils.timeToAge(show.modelRef.birthtime) + '岁');
        $('.qsStatus', this._dom$).text(show.modelRef.modelInfo.status);
        $('.qsNumFollowers', this._dom$).text(show.numLike);

        // Items
        var itemCoversContainer$ = $('.qsSlickItemCovers', this._dom$), slickItemTplt$;
        show.itemRefs.forEach(function(item, index) {
            var slickItem$;
            if (index === 0) {
                slickItem$ = slickItemTplt$ = $('.clone', itemCoversContainer$);
            } else {
                slickItem$ = slickItemTplt$.clone().appendTo(itemCoversContainer$);
            }
            $('.qsItemCover', slickItem$).css('background-image', RenderUtils.imagePathToBackground(item.cover));
            $('.qsItemCover', slickItem$).on('click', function() {
                appRuntime.popup.create('app/components/show/Item', {
                    'data' : item
                }, function(popup) {
                    popup.dom$().css({
                        'height' : '100%',
                        'width' : '100%'
                    });
                    appRuntime.popup.center(popup);
                });
            });
        });
        itemCoversContainer$.slick({
            'dots' : true,
            'arrows' : false,
            'slidesToShow' : 3,
            'slidesToScroll' : 1
        });

        var itemDescriptionContainer$ = $('.qsItemDescriptions', this._dom$), categoryTplt$, nameTplt$;
        show.itemRefs.forEach(function(item, index) {
            var category$, name$;
            if (index === 0) {
                category$ = categoryTplt$ = $('.clone', itemDescriptionContainer$).eq(0);
                name$ = nameTplt$ = $('.clone', itemDescriptionContainer$).eq(1);
            } else {
                category$ = categoryTplt$.clone().appendTo(itemDescriptionContainer$);
                name$ = nameTplt$.clone().appendTo(itemDescriptionContainer$);
            }
            category$.text(CodeUtils.getValue('item.category', item.category));
            name$.text(item.name);
        });
    };

    return Show;
});
