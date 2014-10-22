// @formatter:off
define([
    'ui/containers/IScrollContainer',
    'app/views/ViewBase',
    'app/services/UserService',
    'app/components/common/Header',
    'app/components/user/HairTypeComponent',
    'app/model'
], function(IScrollContainer, ViewBase, UserService, Header, HairTypeComponent, model) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var U05HairType = function(dom) {
        U05HairType.superclass.constructor.apply(this, arguments);

        var header = new Header($('<div/>').appendTo(this._dom$), {
            'title' : '设置',
            'right' : '保存'
        });

        header.on('clickRight', function(event) {
            UserService.update(main.save(), function(metadata, data) {
                if(metadata.error == undefined) {
                    model.user(data).serialize();
                    appRuntime.view.back();
                } else {
                    alert("更新失败");
                }
            });
        });

        var body = new IScrollContainer($('<div/>').css({
            'width' : '100%',
            'height' : this._dom$.height() - header.getPreferredSize().height
        }).appendTo(this._dom$));

        var main = new HairTypeComponent($('<div/>'), model);
        body.append(main);
    };
    andrea.oo.extend(U05HairType, ViewBase);

    return U05HairType;
});

