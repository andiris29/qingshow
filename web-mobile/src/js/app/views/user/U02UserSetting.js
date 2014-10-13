// @formatter:off
define([
    'ui/scroll/IScrollContainer',
    'app/views/ViewBase',
    'app/components/header/CommonHeader',
    'app/components/user/UserSettingComponents'
], function(IScrollContainer, ViewBase, CommonHeader, UserSettingComponents) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var U02UserSetting = function(dom) {
        U02UserSetting.superclass.constructor.apply(this, arguments);

        var header = new CommonHeader($('<div/>').appendTo(this._dom$), {
            'title' : '设置',
            'right' : '保存'
        });

        header.on('clickRight', function(event) {
            console.log('save', main.save());
            // DataService.request('/user/update', main.save(), function() {
            // appRuntime.view.back();
            // });
        });

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
