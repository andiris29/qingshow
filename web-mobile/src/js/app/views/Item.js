// @formatter:off
define([
    'ui/scroll/IScrollContainer',
    'app/views/ViewBase',
    'app/components/Header',
    'app/components/ItemMain',
    'app/components/ItemGallery'
], function(IScrollContainer, ViewBase, Header, ItemMain, ItemGallery) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var Item = function(dom) {
        Item.superclass.constructor.apply(this, arguments);

        var header = new Header($('<div/>').appendTo(this._dom$), '商品 xxx');
        var body = new IScrollContainer($('<div/>').css({
            'width' : '100%',
            'height' : this._dom$.height() - header.getPreferredSize().height
        }).appendTo(this._dom$));

        var main = new ItemMain($('<div/>'));
        var gallery = new ItemGallery($('<div/>'));
        body.append(main);
        body.append(gallery);
    };
    andrea.oo.extend(Item, ViewBase);

    return Item;
});
