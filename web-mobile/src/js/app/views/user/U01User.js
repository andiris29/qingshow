// @formatter:off
define([
    'ui/scroll/IScrollContainer',
    'app/views/ViewBase',
    'app/components/header/BackableHeader',
    'app/components/show/Show',
    'app/components/show/ShowGallery'
], function(IScrollContainer, ViewBase, BackableHeader, ItemMain, ItemGallery) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var U01User = function(dom) {
        U01User.superclass.constructor.apply(this, arguments);

        var header = new BackableHeader($('<div/>').appendTo(this._dom$), '用户 xxx');
    };
    andrea.oo.extend(U01User, ViewBase);

    return U01User;
});
