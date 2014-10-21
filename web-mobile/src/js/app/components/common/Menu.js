// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/services/FeedingService'
], function(UIComponent, TemplateManager, FeedingService) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var Menu = function(dom) {
        Menu.superclass.constructor.apply(this, arguments);

        TemplateManager.load('common/menu.html', function(err, content$) {
            this._dom$.append(content$);

            $('li').on(appRuntime.events.click, function() {
                appRuntime.view.to('app/views/show/S02Feeding', {
                    'feeding' : FeedingService.choosen
                });
                appRuntime.popup.remove(this);
            }.bind(this));
        }.bind(this));
    };

    andrea.oo.extend(Menu, UIComponent);

    Menu.prototype.getPreferredSize = function() {
        return {
            'width' : 640,
            'height' : 510
        };
    };

    return Menu;
});
