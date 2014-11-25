// @formatter:off
define([
    'ui/containers/IScrollContainer',
    'app/views/ViewBase',
    'app/components/common/Header',
    'app/services/UserService',
    'app/components/user/LoginComponent',
    'app/model'
], function(IScrollContainer, ViewBase, Header, UserService, LoginComponent, model) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var U06Login = function(dom) {
        U06Login.superclass.constructor.apply(this, arguments);

        var header = new Header($('<div/>').appendTo(this._dom$), {
            'title' : '登陆',
            'right' : '注册'
        });

        //this._dom$.on(appRuntime.events.click, function() {
        //    UserService.login('qs@qs.com', 'xxxxxx', function(metadata, data) {
        //        model.user(data);
        //        // TODO go U01User
        //        appRuntime.view.back();
        //    }.bind(this));
        //}.bind(this));

        header.on('clickRight', function(event) {
            // TODO GOTO Register
            appRuntime.view.to('app/views/user/U07Register');
        });

        var body = new IScrollContainer($('<div/>').css({
            'width' : '100%',
            'height' : this._dom$.height() - header.getPreferredSize().height
        }).appendTo(this._dom$));

        var main = new LoginComponent($('<div/>'), model);
        body.append(main);
    };
    andrea.oo.extend(U06Login, ViewBase);

    return U06Login;
});
