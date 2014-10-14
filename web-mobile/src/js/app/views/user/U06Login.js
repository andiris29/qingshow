// @formatter:off
define([
    'ui/scroll/IScrollContainer',
    'app/views/ViewBase',
    'app/components/header/CommonHeader',
    'app/services/UserService',
    'app/model'
], function(IScrollContainer, ViewBase, CommonHeader, UserService, model) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var U06Login = function(dom) {
        U06Login.superclass.constructor.apply(this, arguments);

        var header = new CommonHeader($('<div/>').appendTo(this._dom$), '登录');

        this._dom$.on(appRuntime.events.click, function() {
            UserService.login('qs@qs.com', 'xxxxxx', function(metadata, data) {
                model.user(data).serialize();
                // TODO go U01User
                appRuntime.view.back();
            }.bind(this));
        }.bind(this));
    };
    andrea.oo.extend(U06Login, ViewBase);

    return U06Login;
});
