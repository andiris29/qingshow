// @formatter:off
define([
], function() {
// @formatter:on
    var DockToRectangle = {};
    /**
     *
     * @param {UIComponent} ui
     * @param {Object} element$
     * @param {Object} options
     *      align lt/rt/lb/rb
     *      direction up/down/left/right
     *      gap
     */
    DockToRectangle.dock = function(ui, rectangle, options) {
        ui.dom$().css('position', 'absolute');

        var align = options.align, direction = options.direction, gap = options.gap;
        var x = align.substr(0, 1) === 'l' ? rectangle.left() : rectangle.right();
        var y = align.substr(1, 2) === 't' ? rectangle.top() : rectangle.bottom();
        if (direction === 'down' || direction === 'up') {
            ui.dom$().css('left', x);
            ui.dom$().css('top', direction === 'down' ? y + gap : y - ui.dom$().height() - gap);
        }
    };

    return DockToRectangle;
});
