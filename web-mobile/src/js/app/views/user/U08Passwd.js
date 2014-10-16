// @formatter:off
define([
    'ui/scroll/IScrollContainer',
    'app/views/ViewBase',
    'app/components/header/CommonHeader',
    'app/components/user/PasswdComponent',
    'app/model'
], function(IScrollContainer, ViewBase, CommonHeader, PasswdComponent, model) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var U08Passwd = function(dom) {
        U08Passwd.superclass.constructor.apply(this, arguments);

        var header = new CommonHeader($('<div/>').appendTo(this._dom$), {
            'title' : '设置',
            'right' : '保存'
        });

        header.on('clickRight', function(event) {
            // TODO
            // DataService.request('/user/update', main.save(), function() {
                // appRuntime.view.back();
            // });
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



