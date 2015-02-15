// @formatter:off
define([
    'violet/utils/OOUtil',
    'violet/ui/core/UIComponent'
], function(OOUtil, UIComponent) {
// @formatter:on
    var Show = function(dom, options) {
        Show.superclass.constructor.apply(this, arguments);

        this._mongoShow = options.show;

        this._vjs = null;

        this._render();
    };
    OOUtil.extend(Show, UIComponent);

    Show.prototype._render = function() {
        var mongoShow = this._mongoShow;

        var portrait$ = this.$('.qs-portrait');
        portrait$.css('background-image', 'url(' + mongoShow.modelRef.portrait + ')');
        portrait$.css({
            'height' : portrait$.width(),
            'border-radius' : portrait$.width() / 2
        });
        // Video
        var video$ = this.$('.qs-video').attr({
            'width' : this._dom$.width(),
            'height' : this._dom$.height()
        }).hide();
        $('<source/>').attr({
            'type' : 'video/mp4',
            'src' : mongoShow.video
        }).appendTo(video$);
        var vjs = this._vjs = videojs(video$.get(0));
        // Slick
        var slickContainer$ = this.$('.qs-poster-slick-container');
        var slickItemTplt$ = this.$('.qs-poster-slick-container > .clone').removeClass('clone').remove();
        mongoShow.posters.forEach( function(poster, index) {
            var slick$ = slickItemTplt$.clone().appendTo(slickContainer$);
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
    };

    Show.prototype.destroy = function() {
        if (this._vjs) {
            this._vjs.dispose();
        }

        Show.superclass.destroy.apply(this, arguments);
    };

    return Show;
});
