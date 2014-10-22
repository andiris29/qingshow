// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/services/UserService',
    'app/utils/RenderUtils',
    'app/model'
], function(UIComponent, TemplateManager, UserService, RenderUtils, model) {
// @formatter:on

    var RegisterComponent = function(dom) {
        RegisterComponent.superclass.constructor.apply(this, arguments);

        async.parallel([ function(callback) {
            // load template
            TemplateManager.load('user/register.html', true, function(err, content$) {
                this._dom$.append(content$);
                callback(null);
            }.bind(this));
        }.bind(this)], function(callback) {
            this._render();
        }.bind(this));

    };

    andrea.oo.extend(RegisterComponent, UIComponent);

    RegisterComponent.prototype._render = function() {
        $('#register', this._dom$).bind(appRuntime.events.click, function() {
            var main = $('.qsTpltRegisterMain');

            var user = $('#user', main).val();
            var passwd = $('#password', main).val();
            var passwd_confirm = $('#password_confirm', main).val();

            if (user.length == 0) {
                alert("请输入账号");
                return;
            }

            if (passwd.length == 0) {
                alert("请输入密码");
                return;
            }

            if (passwd_confirm != passwd) {
                alert("密码不一致");
                return;
            }

            UserService.register(user, passwd, function(metadata, data) {
                if (metadata.error == undefined) {
                    model.user(data.people).serialize();
                    appRuntime.view.to('app/views/show/S01Home');
                } else {
                    alert("注册失败");
                }
            }.bind(this));
        });
    };

    return RegisterComponent;
});

