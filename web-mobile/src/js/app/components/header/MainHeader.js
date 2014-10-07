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
    var MainHeader = function(dom) {
        MainHeader.superclass.constructor.apply(this, arguments);

        TemplateManager.load('header/main-header.html', function(err, content$) {
            this._dom$.append(content$);

            var menu;
            $('.qsCategoryMenu', content$).on(appRuntime.events.click, function() {
                if (menu) {
                    return;
                }
                menu = appRuntime.popup.create(Menu, true);
                appRuntime.popup.dock(menu, this._dom$, 'lb', 'down', 0);
                menu.on('destroy', function() {
                    menu = null;
                });
            }.bind(this));
        }.bind(this));
    };

    andrea.oo.extend(MainHeader, UIComponent);

    MainHeader.prototype.getPreferredSize = function() {
        return {
            'height' : 96
        };
    };

    return MainHeader;
});
