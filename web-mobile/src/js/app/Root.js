// @formatter:off
define([
    'ui/UIComponent',
    'ui/layout/Rectangle',
    'app/ViewContainer',
    'ui/layout/DockToRectangle'
], function(UIComponent, Rectangle, ViewContainer, DockToRectangle) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var Root = function(dom, width, height) {
        Root.superclass.constructor.apply(this, arguments);
        this._dom$.addClass('qsRoot');

        // Set the size of root
        var scale = this._scale = width / 640;
        this._dom$.css({
            'width' : '640px',
            'height' : height / this._scale + 'px',
            '-webkit-transform' : andrea.string.substitute('scale({0}, {0})', this._scale)
        });

        // Hack the jquery.fn.offset for jquery.lazyload
        var offset = $.fn.offset;
        $.fn.offset = function() {
            var result = offset.apply(this, arguments);
            result = {
                'left' : result.left,
                'top' : result.top / scale
            };
            return result;
        };
        // View container
        this._viewContainer = new ViewContainer($('<div/>').appendTo(this._dom$));
        // Popup
        appRuntime.popup.create = $.proxy(this.createPopup, this), appRuntime.popup.remove = $.proxy(this.removePopup, this);
        appRuntime.popup.dock = $.proxy(this.dockPopup, this);
        this._popups = [];
    };

    andrea.oo.extend(Root, UIComponent);

    Root.prototype.createPopup = function(clazz, closeClickOutside) {
        var popup = new clazz($('<div/>').addClass('qsPopup').appendTo(this._dom$));
        this._popups.push(popup);

        // Close when click outside
        if (closeClickOutside) {
            var html$ = $('html');
            var remove = function() {
                html$.off(appRuntime.events.click);
                appRuntime.popup.remove(popup);
            };
            _.defer(function() {
                html$.on(appRuntime.events.click, remove);
            });

            popup.dom$().on(appRuntime.events.click, function(event) {
                event.stopPropagation();
            });
        }
        // Animation
        new PageTransitions(null, popup.dom$()).nextPage(PageTransitions.animations.createPopup);

        return popup;
    };

    Root.prototype.removePopup = function(popup, callback) {
        this._popups.splice(this._popups.indexOf(popup), 1);

        // Animation
        new PageTransitions(popup.dom$(), null).nextPage(PageTransitions.animations.removePopup, function() {
            popup.destroy();
            if (callback) {
                callback();
            }
        });
    };

    Root.prototype.dockPopup = function(popup, element$, align, direction, gap) {
        var rootRectangle = Rectangle.parseDOM(this._dom$);
        var elementRectangle = Rectangle.parseDOM(element$);

        var left = Math.round((elementRectangle.left() - rootRectangle.left()) / this._scale);
        var top = Math.round((elementRectangle.top() - rootRectangle.top()) / this._scale);
        DockToRectangle.dock(popup, new Rectangle(left, top, elementRectangle.width(), elementRectangle.height()), align, direction, gap);
    };

    return Root;
});
