// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/services/DataService',
    'app/utils/CodeUtils',
    'app/utils/RenderUtils'
], function(UIComponent, TemplateManager, DataService, CodeUtils, RenderUtils) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var UserComparisonMenu = function(dom, data) {
        UserComparisonMenu.superclass.constructor.apply(this, arguments);
        this._user = data;

        TemplateManager.load('user/user-comparison-menu.html', function(err, content$) {
            this._dom$.append(content$);
            this._render();
        }.bind(this));
    };
    andrea.oo.extend(UserComparisonMenu, UIComponent);

    UserComparisonMenu.prototype._render = function() {
        UserComparisonMenu.superclass._render.apply(this, arguments);

        $('.qsComparison', this._dom$).on(appRuntime.events.click, function(event) {
            appRuntime.popup.remove(this);
        }.bind(this));
        $('.qsRemove', this._dom$).on(appRuntime.events.click, function(event) {
            appRuntime.popup.remove(this);
        }.bind(this));

    };
    return UserComparisonMenu;
});
