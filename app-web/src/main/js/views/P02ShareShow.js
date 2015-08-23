// @formatter:off
define([
], function(
    ) {
// @formatter:on
    var P02ShareShow = function(dom, initOptions) {
        P02ShareShow.superclass.constructor.apply(this, arguments);

        __services.httpService.request('/spread/open', 'get', {
            'entry' : violet.url.search.entity || "",
            'initiatorRef' : violet.url.search.initiatorRef || "",
            'targetRef' : violet.url.search.targetRef || ""
        }, function(err, metadata, data) {
        });

        var imageArray = [];
        for (var i = 1; i < 5; i++) {
            imageArray.push(violet.string.substitute("./assets/slicing/p02/share_show_{0}.jpg", i));
        }

        var $doms = $('.p02-image-slider-block-image', this._dom);
        for (var index = 1; index < $doms.size(); index++) {
            var dom = $doms[index];
            $(dom).css('background-image', violet.string.substitute('url({0})', imageArray[index]));
        }

        $(window).resize( function() {
            this._resizeHandler();
        }.bind(this));

        __services.httpService.request('/show/query', 'get', {
            '_ids' : [initOptions._id]
        }, function(err, metadata, data) {
            if (data) {
                var show = data.shows[0];
                var $dom = $($('.p02-image-slider-block-image', this._dom)[0]);
                $dom.css('background-image', violet.string.substitute('url({0})', show.cover));
                $dom.attr('src', show.coverForeground);

                $('.p02-image-slider', this._dom).slick({
                    centerMode : true,
                    slidesToShow : 1,
                    centerPadding : '15%'
                });
                $('.p02-image-slider-block-content', this._dom).show();
                this._resizeHandler();
            }
        }.bind(this));
        $('.p02-image-slider-block-content', this._dom).hide();
        $('.p02-download', this._dom).on('click', __services.downloadService.download);
    };
    violet.oo.extend(P02ShareShow, violet.ui.ViewBase);

    P02ShareShow.prototype._resizeHandler = function() {
        $('.p02-image-slider-block-image', this._dom).css({
            'height' : $('.slick-center', this._dom).width() / 9 * 16 + 'px'
        });
    };

    return P02ShareShow;
});
