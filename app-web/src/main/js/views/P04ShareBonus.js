// @formatter:off
define([
], function(
    ) {
// @formatter:on
    var P04ShareBonus = function(dom, initOptions) {
        P04ShareBonus.superclass.constructor.apply(this, arguments);

        var imageArray = [];
        imageArray.push(__config.image.root + "/assets/slicing/p02/share_show_5.jpg");
        imageArray.push(__config.image.root + "/assets/slicing/p02/share_show_2.jpg");
        imageArray.push(__config.image.root + "/assets/slicing/p02/share_show_3.jpg");
        imageArray.push(__config.image.root + "/assets/slicing/p02/share_show_4.jpg");

        var $doms = $('.p02-image-slider-block-image', this._dom);
        for (var index = 0; index < $doms.size(); index++) {
            var dom = $doms[index];
            $(dom).css('background-image', violet.string.substitute('url({0})', imageArray[index]));
        }

        $(window).resize( function() {
            this._resizeHandler();
        }.bind(this));

        setTimeout(function() {
            $('.p02-image-slider', this._dom).slick({
                'infinite' : true,
                'centerMode' : true,
                'slidesToShow' : 1,
                'centerPadding' : '15%'
            });
            $('.p02-image-slider-block-content', this._dom).show();
        
            this._resizeHandler();
        }.bind(this), 0);
                
        $('.p02-image-slider-block-content', this._dom).hide();
        $('.p02-download', this._dom).on('click', __services.downloadService.download);
    };
    violet.oo.extend(P04ShareBonus, violet.ui.ViewBase);

    P04ShareBonus.prototype._resizeHandler = function() {
        $('.p02-image-slider-block-image', this._dom).css({
            'height' : $('.slick-center', this._dom).width() / 9 * 16 + 'px'
        });
    };

    return P04ShareBonus;
});
