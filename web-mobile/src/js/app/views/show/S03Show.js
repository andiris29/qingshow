// @formatter:off
define([
    'ui/scroll/IScrollContainer',
    'app/views/ViewBase',
    'app/components/header/BackableHeader',
    'app/components/show/Show',
    'app/components/show/ShowGallery'
], function(IScrollContainer, ViewBase, BackableHeader, Show, ShowGallery) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var S03Show = function(dom) {
        S03Show.superclass.constructor.apply(this, arguments);

        var header = new BackableHeader($('<div/>').appendTo(this._dom$), '商品 xxx');
        var body = new IScrollContainer($('<div/>').css({
            'width' : '100%',
            'height' : this._dom$.height() - header.getPreferredSize().height
        }).appendTo(this._dom$));

        var main = new Show($('<div/>'));
        var gallery = new ShowGallery($('<div/>'));
        body.append(main);
        // body.append(gallery);
    };
    andrea.oo.extend(S03Show, ViewBase);

    return S03Show;
});
