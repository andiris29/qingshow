// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager',
], function(UIComponent, TemplateManager, DataService) {
// @formatter:on
  
    /**
     * User Settings's UI Componets
     */
    var UserSettingComponents = function(dom) {
        UserSettingComponents.superclass.constructor.apply(this, arguments);

        async.parallel([ function(callback) {
            // load template
            TemplateManager.load('user/user-Settings', true, function(err, content$) {
                this._dom$.append(content);
                callback(null);
            }.bind(this));
        }.bind(this)], function(err, results) {
            var main$ = results[1];
            main$.appendTo($('.qsTbltUserSettingMain', this._dom$));
            this._render(results[2]);
        }.bind(this));
      
    };
    andrea.oo.extend(UserSettingComponents, UIComponent);

    UserSettingComponents.prototype._render = function(peopeSettingJson) {
    };

    return UserSettingComponents;
});
