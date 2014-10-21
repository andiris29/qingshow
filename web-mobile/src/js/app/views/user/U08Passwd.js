// @formatter:off
define([
    'ui/containers/IScrollContainer',
    'app/views/ViewBase',
    'app/services/DataService',
    'app/components/common/Header',
    'app/components/user/PasswdComponent',
    'app/model'
], function(IScrollContainer, ViewBase, DataService, Header, PasswdComponent, model) {
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
            DataService.request('/user/update', main.save(), function(metadata) {
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

        var main = new PasswdComponent($('<div/>'), model);
        body.append(main);
    };
    andrea.oo.extend(U08Passwd, ViewBase);

    return U08Passwd;
});



