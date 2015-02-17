// @formatter:off
define([
    'violet/utils/OOUtil',
    'violet/utils/InteractionUtil',
    'violet/ui/core/UIComponent'
], function(OOUtil, InteractionUtil, UIComponent) {
// @formatter:on
    var Show = function(dom, options) {
        Show.superclass.constructor.apply(this, arguments);

        this._vjs = null;
        this._videoTplt$ = null;
        this._slickContainer$ = null;
        this._slickItemTplt$ = null;

        this._mongoShow = options.show;

        this._initialize();
    };
    OOUtil.extend(Show, UIComponent);

    Show.prototype.mongoShow = function(value) {
        if (arguments.length) {
            this._mongoShow = value;

            this._render();
        } else {
            return this._mongoShow;
        }
    };

    Show.prototype._initialize = function() {
        // Render
        this._render();
        // Events
        InteractionUtil.onTouchOrClick(this.$('.qs-play'), function(event) {
            this._vjs.show();
            this._vjs.play();
        }.bind(this));
        InteractionUtil.onTouchOrClick(this.$('.qs-pause'), function(event) {
            this._vjs.pause();
        }.bind(this));
    };

    Show.prototype._reset = function() {
        // Reset
        if (this._slickContainer$) {
            this._slickContainer$.slick('unslick');
        }
        if (this._vjs) {
            this._vjs.dispose();
            this._switch(false);
        }
    };

    Show.prototype._render = function() {
        this._reset();
        // Render
        var mongoShow = this._mongoShow;

        var portrait$ = this.$('.qs-portrait');
        portrait$.css('background-image', 'url(' + mongoShow.modelRef.portrait + ')');
        portrait$.css({
            'height' : portrait$.width(),
            'border-radius' : portrait$.width() / 2
        });
        // Video
        if (!this._videoTplt$) {
            this._videoTplt$ = this.$('.qs-video').attr({
                'width' : this._dom$.width(),
                'height' : this._dom$.height()
            }).removeClass('clone').remove();
        }
        var videoContainer$ = this.$('.qs-video-container');
        var video$ = this._videoTplt$.clone().appendTo(videoContainer$);
        $('<source/>').attr({
            'type' : 'video/mp4',
            'src' : mongoShow.video
        }).appendTo(video$);
        var vjs = this._vjs = videojs(video$.get(0));
        vjs.hide();
        vjs.on('play', function() {
            this._switch(true);
        }.bind(this));
        vjs.on('pause', function() {
            this._switch(false);
        }.bind(this));
        vjs.on('ended', function() {
            this._switch(false);
            vjs.load();
        }.bind(this));
        // Slick
        var slickContainer$ = this._slickContainer$ = this.$('.qs-poster-slick-container');
        if (!this._slickItemTplt$) {
            this._slickItemTplt$ = this.$('.qs-poster-slick-container > .clone').removeClass('clone');
        }
        slickContainer$.empty();
        mongoShow.posters.forEach( function(poster, index) {
            var slick$ = this._slickItemTplt$.clone().appendTo(slickContainer$);
            $('.qs-poster', slick$).css({
                'background-image' : 'url(' + poster + ')',
                'height' : this._dom$.height()
            });
        }.bind(this));
        slickContainer$.slick({
            'dots' : true,
            'arrows' : false,
            'slidesToShow' : 1,
            'slidesToScroll' : 1
        });
        //
        this._switch(false);
    };

    Show.prototype.destroy = function() {
        if (this._vjs) {
            this._vjs.dispose();
        }
        this.$('.qs-play').off();
        this.$('.qs-pause').off();

        Show.superclass.destroy.apply(this, arguments);
    };

    Show.prototype._switch = function(playing) {
        var slickContainer$ = this.$('.qs-poster-slick-container');
        var model$ = this.$('.qs-model');
        var play$ = this.$('.qs-play');
        var pause$ = this.$('.qs-pause');
        if (playing) {
            pause$.show();
            slickContainer$.hide();
            model$.hide();
            play$.hide();
        } else {
            pause$.hide();
            slickContainer$.show();
            model$.show();
            play$.show();
        }
    };

    return Show;
});
