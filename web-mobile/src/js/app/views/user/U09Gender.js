// @formatter:off
define([
    'ui/containers/IScrollContainer',
    'app/views/ViewBase',
    'app/services/UserService',
    'app/components/common/Header',
    'app/components/user/GenderComponent',
    'app/utils/CodeUtils',
    'app/model'
], function(IScrollContainer, ViewBase, UserService, Header, GenderComponent, CodeUtils, model) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var U09Gender = function(dom) {
        U09Gender.superclass.constructor.apply(this, arguments);

        var header = new Header($('<div/>').appendTo(this._dom$), {
            'title' : '设置',
            'right' : '保存'
        });

        header.on('clickRight', function(event) {
            UserService.update(main.save(), function(metadata, data) {
                if (metadata.error == undefined) {
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

        var main = new GenderComponent($('<div/>'), model);
        body.append(main);
    };
    andrea.oo.extend(U09Gender, ViewBase);

    return U09Gender;
});
