// @formatter:off
define([
    'violet/utils/OOUtil',
    'violet/ui/core/UIComponent'
], function(OOUtil, UIComponent) {
// @formatter:on
    var Show = function(dom, options) {
        Show.superclass.constructor.apply(this, arguments);

        console.log(options);
    };
    OOUtil.extend(Show, UIComponent);

    return Show;
});
