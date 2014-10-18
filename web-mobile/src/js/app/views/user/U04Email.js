// @formatter:off
define([
    'ui/scroll/IScrollContainer',
    'app/views/ViewBase',
    'app/services/DataService',
    'app/components/header/CommonHeader',
    'app/components/user/EmailComponent',
    'app/model'
], function(IScrollContainer, ViewBase, DataService, CommonHeader, EmailComponent, model) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var U04Email = function(dom) {
        U04Email.superclass.constructor.apply(this, arguments);

        var header = new CommonHeader($('<div/>').appendTo(this._dom$), {
            'title' : '设置',
            'right' : '保存'
        });

        header.on('clickRight', function(event) {
            if (!main.validate()) {
                return;
            }
            DataService.request('/user/updateEmail', main.save(), function(metadata) {
                if (metadata.result == 0) {
                    appRuntime.view.back();
                } else {
                    alert("邮件地址更新失败");
                }
             });
        });

        var body = new IScrollContainer($('<div/>').css({
            'width' : '100%',
            'height' : this._dom$.height() - header.getPreferredSize().height
        }).appendTo(this._dom$));

        var main = new EmailComponent($('<div/>'), model);
        body.append(main);
    };
    andrea.oo.extend(U04Email, ViewBase);

    return U04Email;
});


