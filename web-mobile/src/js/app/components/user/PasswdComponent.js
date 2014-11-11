// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/services/UserService',
    'app/utils/RenderUtils'
], function(UIComponent, TemplateManager, UserService, RenderUtils) {
// @formatter:on

    var PasswdComponent = function(dom) {
        PasswdComponent.superclass.constructor.apply(this, arguments);

        async.parallel([ function(callback) {
            // load template
            TemplateManager.load('user/change-passwd.html', true, function(err, content$) {
                this._dom$.append(content$);
                callback(null);
            }.bind(this));
        }.bind(this)], function(callback) {
        }.bind(this));

    };

    andrea.oo.extend(PasswdComponent, UIComponent);

    PasswdComponent.prototype.validate = function() {
        var view$ = $('.qsTpltPasswdMain', this._dom$);
        var nowPasswd = $('#nowPasswd', view$).val();
        var newPasswd = $('#newPasswd', view$).val();
        var confirmPasswd= $('#confirmPasswd', view$).val();

        if (nowPasswd.length == 0) {
            alert("请输入当前密码");
            return false;
        }
        if (!RenderUtils.checkStringMatchPattern(RenderUtils.PASSWORD_REGEXP, newPasswd)) {
            alert("当前密码格式不正确,请输入8-12位的英文或数字");
            return false;
        }
        if (newPasswd.length == 0) {
            alert("请输入新密码");
            return false;
        }

        if (!RenderUtils.checkStringMatchPattern(RenderUtils.PASSWORD_REGEXP, newPasswd)) {
            alert("新密码格式不正确请,输入8-12位的英文或数字");
            return false;
        }

        if ((newPasswd.length > 0) && (newPasswd != confirmPasswd)) {
            alert("密码不一致");
            return false;
        }

        var nowEncryptPasswd = UserService.encrypt(nowPasswd).toString();
        if (this._data._user.userInfo.encryptedPassword != nowEncryptPasswd) {
            alert("当前密码不正确！");
            return false;
        }

        return true;
    };

    PasswdComponent.prototype.save = function() {
        var view$ = $('.qsTpltPasswdMain', this._dom$);
        var newPasswd = $('#newPasswd', view$).val();
        return {
            "password": newPasswd,
        };
    };

    return PasswdComponent;
});

