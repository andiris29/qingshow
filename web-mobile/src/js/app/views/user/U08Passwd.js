// @formatter:off
define([
    'ui/containers/IScrollContainer',
    'app/views/ViewBase',
    'app/services/UserService',
    'app/components/common/Header',
    'app/components/user/PasswdComponent',
    'app/utils/CodeUtils',
    'app/model'
], function(IScrollContainer, ViewBase, UserService, Header, PasswdComponent, CodeUtils, model) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var U08Passwd = function(dom) {
        U08Passwd.superclass.constructor.apply(this, arguments);

        var header = new Header($('<div/>').appendTo(this._dom$), {
            'title' : '设置',
            'right' : '保存'
        });

        header.on('clickRight', function(event) {
            if (!main.validate()) {
                return;
            }
            UserService.update(main.save(), function(metadata, data) {
                if(metadata.error == undefined) {
                    model.user(data);
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

        var main = new PasswdComponent($('<div/>'), model);
        body.append(main);
    };
    andrea.oo.extend(U08Passwd, ViewBase);

    return U08Passwd;
});
