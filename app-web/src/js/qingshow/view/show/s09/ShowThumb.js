// @formatter:off
define([
    'violet/utils/OOUtil',
    'violet/ui/core/UIComponent'
], function(OOUtil, UIComponent) {
// @formatter:on
    var ShowThumb = function(dom, options) {
        ShowThumb.superclass.constructor.apply(this, arguments);
                console.log(options);
    };
    OOUtil.extend(ShowThumb, UIComponent);

    return ShowThumb;
});
