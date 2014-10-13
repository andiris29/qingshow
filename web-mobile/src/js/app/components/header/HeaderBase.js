// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/components/Menu'
], function(UIComponent, TemplateManager, Menu) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var HeaderBase = function(dom) {
        HeaderBase.superclass.constructor.apply(this, arguments);
    };

    andrea.oo.extend(HeaderBase, UIComponent);

    HeaderBase.prototype.title = function(value) {
        $('.qsTitle', this._dom$).text(value);
    };

    HeaderBase.prototype.getPreferredSize = function() {
        return {
            'height' : 96
        };
    };

    // _onNext

    return HeaderBase;
});
