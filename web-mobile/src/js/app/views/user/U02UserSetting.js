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
    var U02UserSetting = function(dom) {
        U02UserSetting.superclass.constructor.apply(this, arguments);

        var header = new BackableHeader($('<div/>').appendTo(this._dom$), '设置');
    };
    andrea.oo.extend(U02UserSetting, ViewBase);

    return U02UserSetting;
});
