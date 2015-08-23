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
        }, function(err, metadata, data) {});


        var imageArray = [];
        for (var i = 1; i < 5; i++) {
            imageArray.push(violet.string.substitute("./assets/slicing/p02/share_show_{0}.png", i));
        }

        var $doms = $('.p02-image-slider-block-image', this._dom);
        for (var index = 1; index < $doms.size(); index++) {
            var dom = $doms[index];
            $(dom).css('background-image', violet.string.substitute('url({0})', imageArray[index]));
        }

        $(window).resize(function(){
            this.handleResize();
        }.bind(this));

        __services.httpService.request('/show/query', 'get', {
            '_ids' : [initOptions._id]
        }, function(err, metadata, data) {
            if (data) {
                var show = data.shows[0];
                var $dom = $($('.p02-image-slider-block-image', this._dom)[0]);
                $dom.css('background-image', violet.string.substitute('url({0})', show.cover));
                $dom.attr('src', show.coverForeground);

                $('.p02-image-slider', this._dom).slick();
            }
        }.bind(this));
        $('.p02-download', this._dom).on('click', __services.downloadService.download);

        $(window).trigger('resize');
    };
    violet.oo.extend(P02ShareShow, violet.ui.ViewBase);

    P02ShareShow.prototype.handleResize = function () {
        //420 760
        var height = document.documentElement.clientHeight;
        var width = document.documentElement.clientWidth;
        var slideWidth = width;
        var slideHeight = height * 0.88;

        var marginTopbottom = 30;
        var textHeight = 30;
        var imageHeight = slideHeight - 2 * marginTopbottom - textHeight;
        var imageWidth = imageHeight * 420 / 760;
        var marginLeftRight = (slideWidth - imageWidth) / 2;


        $('.p02-image-slider-block-content', this._dom).css({'width' : slideWidth, 'height' : slideHeight});


        $('.p02-image-slider-block-image-container', this._dom).css({
            'width' : imageWidth,
            'height' : imageHeight,
            'margin-left' : marginLeftRight,
            'margin-right' : marginLeftRight,
            'margin-top' : marginTopbottom,
            'margin-bottom' : marginTopbottom,
        });

        $('.p02-image-slider-block-image', this._dom).css({
            'width' : imageWidth,
            'height' : imageHeight
        });
    };

    return P02ShareShow;
});