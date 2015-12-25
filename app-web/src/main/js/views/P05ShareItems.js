// @formatter:off
define([
], function(
    ) {
// @formatter:on
    var P05ShareItems = function(dom, initOptions) {
        P05ShareItems.superclass.constructor.apply(this, arguments);
  
        var strHTML = "";

        for (var i = 0; i <= 10; i++) {

            strHTML += "<div class=\"win pull-left\">"
            strHTML += "        <div class=\"win-thumbnail\">"
            strHTML += "            <img src=\"images/02.jpg\" class=\"win-img\" />"
            strHTML += "           <span class=\"flag-label\">5折</span>"
            strHTML += "        </div>"
            strHTML += "        <div class=\"win-info\">"
            strHTML += "            <p class=\"win-title\">H&M驼色大衣</p>"
            strHTML += "            <p class=\"price clearfix\"><em class=\"pull-left\">￥450.00</em> <del class=\"pull-right\">￥500.00</del></p>"
            strHTML += "        </div></div><!-- /.win -->"

        };

        $(".winlist").html(strHTML);

        
    violet.oo.extend(P05ShareItems, violet.ui.ViewBase);

    P05ShareItems.prototype._resizeHandler = function() {
        $('.p02-image-slider-block-image', this._dom).css({
            'height' : $('.slick-center', this._dom).width() / 9 * 16 + 'px'
        });
    };

    return P05ShareItems;
});
