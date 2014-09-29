// @formatter:off
define([
    'ui/UIComponent',
    'app/ViewContainer'
], function(UIComponent, ViewContainer) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var ViewBase = function(dom) {
        ViewBase.superclass.constructor.apply(this, arguments);
        this._dom$.addClass('qsView');
    };
    andrea.oo.extend(ViewBase, UIComponent);

    return ViewBase;
});
