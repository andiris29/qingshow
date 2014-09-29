// @formatter:off
define([
], function() {
// @formatter:on
    var DockToRectangle = {};
    /**
     *
     * @param {UIComponent} ui
     * @param {Object} element$
     * @param {string} align lt/rt/lb/rb
     * @param {string} direction up/down/left/right
     * @param {int} gap
     */
    DockToRectangle.dock = function(ui, rectangle, align, direction, gap) {
        ui.dom$().css('position', 'absolute');

        if (direction === 'down' || direction === 'up') {
            ui.dom$().css('left', align.substr(0, 1) === 'l' ? rectangle.left() : rectangle.right());
            ui.dom$().css('top', direction === 'down' ? rectangle.bottom() + gap : rectangle.top() - gap);
        }
    };

    return DockToRectangle;
});
