// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/components/Menu'
], function(UIComponent, TemplateManager, Menu) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var HeaderBase = function(dom) {
        HeaderBase.superclass.constructor.apply(this, arguments);
    };

    andrea.oo.extend(HeaderBase, UIComponent);

    HeaderBase.prototype.title = function(value) {
        $('.qsTitle', this._dom$).text(value);
    };

    HeaderBase.prototype.getPreferredSize = function() {
        return {
            'height' : 96
        };
    };

    HeaderBase.prototype._onMenu = function() {
        var menu;
        $('.qsMenu', this._dom$).on(appRuntime.events.click, function() {
            if (menu) {
                return;
            }
            require(['app/components/Menu'], function(Menu) {
                menu = appRuntime.popup.create(Menu, true);
                appRuntime.popup.dock(menu, this._dom$, 'lb', 'down', 0);
                menu.on('destroy', function() {
                    menu = null;
                });
            }.bind(this));
        }.bind(this));

        return this;
    };

    HeaderBase.prototype._onUser = function() {
        $('.qsUser', this._dom$).on(appRuntime.events.click, function() {
            require(['app/views/user/U02UserSetting'], function(U02UserSetting) {
                appRuntime.view.to(U02UserSetting);
            });
        }.bind(this));

        return this;
    };

    HeaderBase.prototype._onBack = function() {
        $('.qsBack', this._dom$).on(appRuntime.events.click, function() {
            appRuntime.view.back();
        }.bind(this));

        return this;
    };

    return HeaderBase;
});
