// @formatter:off
define([
    'ui/scroll/IScrollContainer',
    'app/views/ViewBase',
    'app/components/header/CommonHeader',
    'app/components/show/Show',
    'app/components/show/ShowGallery'
], function(IScrollContainer, ViewBase, CommonHeader, Show, ShowGallery) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var S03Show = function(dom, data) {
        S03Show.superclass.constructor.apply(this, arguments);
        this._show = data.show;

        var header = new CommonHeader($('<div/>').appendTo(this._dom$), this._show.name);
        var body = new IScrollContainer($('<div/>').css({
            'width' : '100%',
            'height' : this._dom$.height() - header.getPreferredSize().height
        }).appendTo(this._dom$));

        var main = new Show($('<div/>'), this._show);
        body.append(main);

        this.on('destroying', function() {
            main.destroy();
        });
    };
    andrea.oo.extend(S03Show, ViewBase);

    return S03Show;
});
