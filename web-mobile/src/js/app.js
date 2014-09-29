// @formatter:off
define([
    'app/Root',
], function(Root) {
// @formatter:on
    /**
     * Bootstrap the application
     */

    var dom = $(document.body).children()[0], dom$ = $(dom);
    // Ref: iPhone 4: 960 x 640
    // Ref: iPhone 5: 1136 x 640
    var screenW = $(window).width(), screenH = $(window).height();
    var ratio = screenW < screenH ? screenW / screenH : 640 / 960;
    var height = Math.max(0, screenH), width = ratio * height;

    dom$.css({
        'width' : width + 'px',
        'height' : height + 'px'
    });
    new Root($('<div/>').appendTo(dom$), width, height);
});
