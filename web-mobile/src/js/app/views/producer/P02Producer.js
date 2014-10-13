// @formatter:off
define([
    'ui/scroll/IScrollContainer',
    'app/views/ViewBase',
    'app/components/header/CommonHeader',
    'app/components/show/ShowGallery',
], function(IScrollContainer, ViewBase, CommonHeader, ItemGallery) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var P02Producer = function(dom) {
        P02Producer.superclass.constructor.apply(this, arguments);

        var header = new CommonHeader($('<div/>').appendTo(this._dom$), '网红 xxx');
        var body = new IScrollContainer($('<div/>').css({
            'width' : '100%',
            'height' : this._dom$.height() - header.getPreferredSize().height
        }).appendTo(this._dom$));

        var gallery = new ItemGallery($('<div/>'));
        body.append(gallery);
    };
    andrea.oo.extend(P02Producer, ViewBase);

    return P02Producer;
});
