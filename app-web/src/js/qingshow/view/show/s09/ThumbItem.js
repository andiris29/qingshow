// @formatter:off
define([
    'violet/utils/OOUtil',
    'violet/ui/core/UIComponent'
], function(OOUtil, UIComponent) {
// @formatter:on
    var ThumbItem = function(dom, options) {
        ThumbItem.superclass.constructor.apply(this, arguments);

        this._mongoItem = options.item;
        this._render();
    };
    OOUtil.extend(ThumbItem, UIComponent);

    ThumbItem.prototype._render = function() {
        this.$('.qs-image').css('background-image', 'url(' + this._mongoItem.images[0].url + ')');
        var price$ = this.$('.qs-price');
        price$.text(Number(this._mongoItem.brandDiscountInfo ? this._mongoItem.brandDiscountInfo.price : this._mongoItem.price).toFixed(2));
        price$.css('line-height', price$.height() + 2 + 'px');
    };
    return ThumbItem;
});
