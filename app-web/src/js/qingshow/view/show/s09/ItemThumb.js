// @formatter:off
define([
    'violet/utils/OOUtil',
    'violet/ui/core/UIComponent'
], function(OOUtil, UIComponent) {
// @formatter:on
    var ItemThumb = function(dom, options) {
        ItemThumb.superclass.constructor.apply(this, arguments);
                console.log(options);
    };
    OOUtil.extend(ItemThumb, UIComponent);

    return ItemThumb;
});
