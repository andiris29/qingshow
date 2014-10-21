// @formatter:off
define([
    'ui/UIContainer'
], function(UIContainer) {
// @formatter:on
    /**
     *
     * @param {Object} dom
     */
    var SlickContainer = function(dom, config) {
        SlickContainer.superclass.constructor.apply(this, arguments);

        this._slicker = null;
        // TODO trigger resize when adaptiveHeight
        this._options = config.options;
    };
    andrea.oo.extend(SlickContainer, UIContainer);

    /**
     *
     * @param {Object} uiComponent
     * uiComponent.expand will be invoke when scroll to bottom.
     */
    SlickContainer.prototype.append = function(uiComponent) {
        uiComponent.dom$().css({
            'float' : 'left',
            'padding' : '0'
        });
        this.invalidate();
        SlickContainer.superclass.append.apply(this, arguments);
    };

    SlickContainer.prototype.validate = function() {
        SlickContainer.superclass.validate.apply(this, arguments);

        if (this._slicker) {
            this._slicker.unslick();
        }
        // this._slicker = this.dom$().slick(this._options);
    };

    return SlickContainer;
});
