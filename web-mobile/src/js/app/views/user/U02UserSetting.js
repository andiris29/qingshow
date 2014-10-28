// @formatter:off
define([
    'ui/containers/IScrollContainer',
    'app/views/ViewBase',
    'app/services/UserService',
    'app/components/common/Header',
    'app/components/user/UserSettingComponents',
    'app/utils/CodeUtils',
    'app/model'
], function(IScrollContainer, ViewBase, UserService, Header, UserSettingComponents, CodeUtils, model) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var U02UserSetting = function(dom) {
        U02UserSetting.superclass.constructor.apply(this, arguments);

        var header = new Header($('<div/>').appendTo(this._dom$), {
            'title' : '设置',
            'right' : '保存'
        });

        header.on('clickRight', function(event) {
            if (!main.validate()) {
                return;
            }
            UserService.update(main.save(), function(metadata, data) {
                if (metadata.error == undefined) {
                    model.user(data.people).serialize();
                    appRuntime.view.back();
                } else {
                    var err = CodeUtils.getValue('server.error', metadata.error);
                    alert(err);
                }
            });
        });

        var body = new IScrollContainer($('<div/>').css({
            'width' : '100%',
            'height' : this._dom$.height() - header.getPreferredSize().height
        }).appendTo(this._dom$));

        var main = new UserSettingComponents($('<div/>'), model);
        body.append(main);
    };
    andrea.oo.extend(U02UserSetting, ViewBase);

    U02UserSetting.prototype.logout = function() {
        model.user(null).serialize();
    };

    return U02UserSetting;
});
