// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/services/DataService',
    'app/utils/RenderUtils'
], function(UIComponent, TemplateManager, DataService, CodeUtils, RenderUtils) {
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
            this._render();
        }.bind(this));

    };

    andrea.oo.extend(PasswdComponent, UIComponent);

    PasswdComponent.prototype._render = function() {

        //var element = this._data._user.gender;

    };

    PasswdComponent.prototype.save = function() {
    };

    return PasswdComponent;
});

