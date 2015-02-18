define([], function() {
    var InteractionUtil = {};

    InteractionUtil.onTouchOrClick = function(dom$, callback) {
        dom$.on('touchstart click', function(event) {
            // Use preventDefault() inside touch event handlers, so the default mouse-emulation handling doesn’t occur.
            if (event.type === 'touchstart') {
                event.preventDefault();
            }
            callback(event);
        });
    };

    return InteractionUtil;
});
