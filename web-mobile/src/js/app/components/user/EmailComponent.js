// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/services/DataService',
    'app/utils/RenderUtils'
], function(UIComponent, TemplateManager, DataService, CodeUtils, RenderUtils) {
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

        //var element = this._data._user.gender;

    };

    EmailComponent.prototype.save = function() {
    };

    return EmailComponent;
});
