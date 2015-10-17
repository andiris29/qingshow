// @formatter:off
define([
], function(
    ) {
// @formatter:on
    var P04ShareBonus = function(dom, initOptions) {
        P04ShareBonus.superclass.constructor.apply(this, arguments);

        var imageArray = [];
        imageArray.push(__config.image.root + "/assets/slicing/p02/share_bonu_bg.png");
        imageArray.push(__config.image.root + "/assets/slicing/common/a2.jpg");
        imageArray.push(__config.image.root + "/assets/slicing/common/a3.jpg");
        imageArray.push(__config.image.root + "/assets/slicing/common/a4.jpg");

        var $doms = $('.p02-image-slider-block-image', this._dom);
        for (var index = 0; index < $doms.size(); index++) {
            var dom = $doms[index];
            $(dom).css('background-image', violet.string.substitute('url({0})', imageArray[index]));
        }

        $(window).resize( function() {
            this._resizeHandler();
        }.bind(this));

        $('.p02-image-slider-block-content', this._dom).hide();
        $('.p02-download', this._dom).on('click', __services.downloadService.download);

        var bonus = initOptions.entity;
        var totalBonus = parseFloat(bonus.total).toFixed(2);
        var withdrawBonus = parseFloat(bonus.withdrawTotal).toFixed(2);
        $('.p02-bonus-total-text-number', this._dom)[0].innerText = '￥' + totalBonus;
        $('.p02-bonus-current-text-number', this._dom)[0].innerText = '￥' + withdrawBonus;

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

    };
    violet.oo.extend(P04ShareBonus, violet.ui.ViewBase);

    P04ShareBonus.prototype._resizeHandler = function() {
        $('.p02-image-slider-block-image', this._dom).css({
            'height' : $('.slick-center', this._dom).width() / 9 * 16 + 'px'
        });
    };

    return P04ShareBonus;
});
