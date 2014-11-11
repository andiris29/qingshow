// @formatter:off
define([
    'app/Root',
    'app/services/SerializationService'
], function(Root, SerializationService) {
// @formatter:on
    /**
     * Bootstrap the application
     */
    SerializationService.deserializeLoginUser(function() {
        var dom = $(document.body).children()[0], dom$ = $(dom);
        // iPhone 6 Plus: 1920-by-1080-pixel resolution at 401 ppi
        // iPhone 6: 1334-by-750-pixel resolution at 326 ppi
        // iPhone 5s: 1136-by-640-pixel resolution at 326 ppi
        // iPhone 5c: 1136-by-640-pixel resolution at 326 ppi
        var screenW = $(window).width(), screenH = window.innerHeight ? window.innerHeight : $(window).height();
        var ratio = screenW < screenH ? screenW / screenH : 640 / 960;
        var height = Math.max(0, screenH), width = ratio * height;

        dom$.css({
            'width' : width + 'px',
            'height' : height + 'px'
        });
        new Root($('<div/>').appendTo(dom$), width, height);
    });
});
