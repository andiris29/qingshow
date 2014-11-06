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
                'left' : result.left / scale,
                'top' : result.top / scale
            };
            return result;
        };
        // View container
        this._viewContainer = new ViewContainer($('<div/>').appendTo(this._dom$));
        // Popup
        appRuntime.popup.create = $.proxy(this.createPopup, this), appRuntime.popup.remove = $.proxy(this.removePopup, this);
        appRuntime.popup.dock = $.proxy(this.dockPopup, this), appRuntime.popup.center = $.proxy(this.centerPopup, this);
        this._popups = [];
    };

    andrea.oo.extend(Root, UIComponent);

    /**
     *
     * @param {Object} clazz
     * @param {Object} options
     *      data
     *      closeClickOutside
     * @param {Object} callback
     */
    Root.prototype.createPopup = function(clazz, options, callback) {
        if (_.isString(clazz)) {
            var args = Array.prototype.slice.call(arguments, 0);
            require([clazz], function(clazz) {
                args[0] = clazz;
                appRuntime.popup.create.apply(this, args);
            }.bind(this));
        } else {
            options = options || {};

            var popup = new clazz($('<div/>').addClass('qsPopup').appendTo(this._dom$), options.data);
            this._popups.push(popup);

            // Close when click outside
            if (options.closeClickOutside) {
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

            if (callback) {
                callback(popup);
            }
        }
    };

    Root.prototype.removePopup = function(popup, callback) {
        if (this._popups.indexOf(popup) === -1) {
            return;
        }

        this._popups.splice(this._popups.indexOf(popup), 1);

        // Animation
        new PageTransitions(popup.dom$(), null).nextPage(PageTransitions.animations.removePopup, function() {
            popup.destroy();
            if (callback) {
                callback();
            }
        });
    };

    Root.prototype.dockPopup = function(popup, element$, options) {
        var rootRectangle = Rectangle.parseDOM(this._dom$);
        var elementRectangle = Rectangle.parseDOM(element$);

        var left = Math.round(elementRectangle.left() - rootRectangle.left());
        var top = Math.round(elementRectangle.top() - rootRectangle.top());
        DockToRectangle.dock(popup, new Rectangle(left, top, elementRectangle.width(), elementRectangle.height()), options);
    };

    Root.prototype.centerPopup = function(popup) {
        var rootRectangle = Rectangle.parseDOM(this._dom$);
        var popupRectangle = Rectangle.parseDOM(popup.dom$());

        var left = Math.round((rootRectangle.width() - popupRectangle.width()) / 2);
        var top = Math.round((rootRectangle.height() - popupRectangle.height()) / 2);
        DockToRectangle.dock(popup, new Rectangle(left, top, 0, 0), {
            'align' : 'lb',
            'direction' : 'down',
            'gap' : 0
        });
    };
    return Root;
});
