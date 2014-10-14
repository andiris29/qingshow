// @formatter:off
define([
    'app/managers/TemplateManager',
    'app/components/header/HeaderBase',
    'app/model'
], function(TemplateManager, HeaderBase, model) {
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
            if (model.user()) {
                // TODO go U01User
                appRuntime.view.to('app/views/user/U02UserSetting');
            } else {
                appRuntime.view.to('app/views/user/U06Login');
            }
        }.bind(this));

        return this;
    };

    return MainHeader;
});
