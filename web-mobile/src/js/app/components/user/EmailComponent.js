// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/services/DataService',
    'app/utils/RenderUtils'
], function(UIComponent, TemplateManager, DataService, RenderUtils) {
// @formatter:on

    var EmailComponent = function(dom) {
        EmailComponent.superclass.constructor.apply(this, arguments);

        async.parallel([ function(callback) {
            // load template
            TemplateManager.load('user/change-email.html', true, function(err, content$) {
                this._dom$.append(content$);
                callback(null);
            }.bind(this));
        }.bind(this)], function(callback) {
            this._render();
        }.bind(this));

    };

    andrea.oo.extend(EmailComponent, UIComponent);

    EmailComponent.prototype._render = function() {

        var mail = this._data._user.userInfo.mail;
        var view$ = $('.qsTpltEmailMain', this._dom$);

        $('.qsNowEmail', view$).text(mail);

    };

    EmailComponent.prototype.validate = function() {
        var view$ = $('.qsTpltEmailMain', this._dom$);

        var newEmail = $("#newEmail", view$);
        var confirmEmail = $("#confirmEmail", view$);

        if (newEmail.val().length == 0) {
            alert('请输入新的邮件地址');
            return false;
        }

        if (!RenderUtils.checkStringMatchPattern(RenderUtils.EMAIL_REGEXP, newEmail.val())) {
            alert('邮件地址的格式不正确');
            return false;
        }

        if (newEmail.val() != confirmEmail.val()) {
            alert('输入的邮件地址不一致');
            return false;
        }

        return true;
    }

    EmailComponent.prototype.save = function() {
        var view$ = $('.qsTpltEmailMain', this._dom$);

        var newEmail = $("#newEmail", view$);

        var data = [];
        data['people_id'] = this._data._user.id;
        data['new_mail'] = newEmail.val();

        return {
            "mail": newEmail.val()
        };
    };

    return EmailComponent;
});
