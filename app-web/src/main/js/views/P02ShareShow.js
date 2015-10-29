// @formatter:off
define([
], function(
    ) {
// @formatter:on
    var P02ShareShow = function(dom, initOptions) {
        P02ShareShow.superclass.constructor.apply(this, arguments);
        var show = initOptions.entity;
        
        __services.httpService.request('/show/view', 'post', {
            '_id' : show._id || ""
        }, function(err, metadata, data) {
        });

        var imageArray = [''];
        imageArray.push(__config.image.root + "/assets/slicing/common/a3.jpg");
        imageArray.push(__config.image.root + "/assets/slicing/common/a4.jpg");
        imageArray.push(__config.image.root + "/assets/slicing/common/a1.jpg");
        imageArray.push(__config.image.root + "/assets/slicing/common/a2.jpg");

        var $doms = $('.p02-image-slider-block-image', this._dom);
        for (var index = 1; index < $doms.size(); index++) {
            var dom = $doms[index];
            $(dom).css('background-image', violet.string.substitute('url({0})', imageArray[index]));
        }

        
        var $dom = $($('.p02-image-slider-block-image', this._dom)[0]);
        $dom.css('background-image', violet.string.substitute('url({0})', show.cover));
        $dom.attr('src', show.coverForeground);

        $(window).resize( function() {
            //TODO workaround
            setTimeout(function (){
                this._resizeHandler();
            }.bind(this), 100);
        }.bind(this));

        $('.p02-image-slider-block-content', this._dom).hide();
        $('.p02-download', this._dom).on('click', __services.downloadService.download);

        setTimeout(function () {
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
    violet.oo.extend(P02ShareShow, violet.ui.ViewBase);

    P02ShareShow.prototype._resizeHandler = function() {
        $('.p02-image-slider-block-image', this._dom).css({
            'height' : $('.slick-center', this._dom).width() / 9 * 16 + 'px'
        });
    };

    return P02ShareShow;
});
