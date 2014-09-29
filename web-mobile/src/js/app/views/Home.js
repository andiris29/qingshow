// @formatter:off
define([
    'ui/scroll/IScrollContainer',
    'app/views/ViewBase',
    'app/components/HomeHeader',
    'app/components/ItemGallery',
], function(IScrollContainer, ViewBase, HomeHeader, ItemGallery) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var Home = function(dom) {
        Home.superclass.constructor.apply(this, arguments);

        var header = new HomeHeader($('<div/>').appendTo(this._dom$));
        var body = new IScrollContainer($('<div/>').css({
            'width' : '100%',
            'height' : this._dom$.height() - header.getPreferredSize().height
        }).appendTo(this._dom$));

        var gallery = new ItemGallery($('<div/>'));
        body.append(gallery);
    };
    andrea.oo.extend(Home, ViewBase);

    return Home;
});
