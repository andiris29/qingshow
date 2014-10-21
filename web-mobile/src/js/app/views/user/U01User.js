// @formatter:off
define([
    'ui/containers/IScrollContainer',
    'app/views/ViewBase',
    'app/components/header/CommonHeader',
    'app/components/show/Show',
    'app/components/show/ShowGallery'
], function(IScrollContainer, ViewBase, CommonHeader, ItemMain, ItemGallery) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var U01User = function(dom) {
        U01User.superclass.constructor.apply(this, arguments);

        var header = new CommonHeader($('<div/>').appendTo(this._dom$), '用户 xxx');
    };
    andrea.oo.extend(U01User, ViewBase);

    return U01User;
});
