// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/services/UserService',
    'app/utils/RenderUtils',
    'app/model'
], function(UIComponent, TemplateManager, UserService, RenderUtils, model) {
// @formatter:on

    var LoginComponent = function(dom) {
        LoginComponent.superclass.constructor.apply(this, arguments);

        async.parallel([ function(callback) {
            // load template
            TemplateManager.load('user/login.html', true, function(err, content$) {
                this._dom$.append(content$);
                callback(null);
            }.bind(this));
        }.bind(this)], function(callback) {
            this._render();
        }.bind(this));

    };

    andrea.oo.extend(LoginComponent, UIComponent);

    LoginComponent.prototype._render = function() {
        $('#login', this._dom$).bind(appRuntime.events.click, function() {
            var main = $('.qsTpltLoginMain');

            var user = $('#user', main).val();
            var passwd = $('#password', main).val();

            if (user.length == 0) {
                alert("请输入账号");
                return;
            }
            
            if (passwd.length == 0) {
                alert("请输入密码");
                return;
            }

            UserService.login(user, passwd, function(metadata, data) {
                model.user(data).serialize();
                appRuntime.view.back();
            }.bind(this));
        });
    };

    return LoginComponent;
});

