// @formatter:off
define([
    'violet/utils/OOUtil',
    'violet/ui/core/UIComponent'
], function(OOUtil, UIComponent) {
// @formatter:on
    var ItemThumb = function(dom, options) {
        ItemThumb.superclass.constructor.apply(this, arguments);

        this._mongoItem = options.item;
        this._render();
    };
    OOUtil.extend(ItemThumb, UIComponent);

    ItemThumb.prototype._render = function() {
        this.$('.qs-image').css('background-image', 'url(' + this._mongoItem.images[0].url + ')');
    };
    return ItemThumb;
});
