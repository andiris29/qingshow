// @formatter:off
define([
    'app/managers/TemplateManager',
    'app/components/header/HeaderBase'
], function(TemplateManager, HeaderBase) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    // TODO Merge into CommonHeader
    var MainHeader = function(dom) {
        MainHeader.superclass.constructor.apply(this, arguments);

        TemplateManager.load('header/main-header.html', function(err, content$) {
            this._dom$.append(content$);
            this._onMenu()._onUser();
        }.bind(this));
    };
    andrea.oo.extend(MainHeader, HeaderBase);

    MainHeader.prototype._onMenu = function() {
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

    MainHeader.prototype._onUser = function() {
        $('.qsRight', this._dom$).on(appRuntime.events.click, function() {
            require(['app/views/user/U02UserSetting'], function(U02UserSetting) {
                appRuntime.view.to(U02UserSetting);
            });
        }.bind(this));

        return this;
    };

    return MainHeader;
});
