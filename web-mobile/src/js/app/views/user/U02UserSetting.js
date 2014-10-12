// @formatter:off
define([
    'ui/scroll/IScrollContainer',
    'app/views/ViewBase',
    'app/components/header/SaveableHeader',
    'app/components/user/UserSettingComponents'
], function(IScrollContainer, ViewBase, SaveableHeader, UserSettingComponents) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var U02UserSetting = function(dom) {
        U02UserSetting.superclass.constructor.apply(this, arguments);

        var header = new SaveableHeader($('<div/>').appendTo(this._dom$), '设置');

        var body = new IScrollContainer($('<div/>').css({
            'width' : '100%',
            'height' : this._dom$.height() - header.getPreferredSize().height
        }).appendTo(this._dom$));

        var main = new UserSettingComponents($('<div/>'));
        body.append(main);
    };
    andrea.oo.extend(U02UserSetting, ViewBase);

    return U02UserSetting;
});
