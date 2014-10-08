// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager'
], function(UIComponent, TemplateManager) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var Menu = function(dom) {
        Menu.superclass.constructor.apply(this, arguments);

        async.parallel([
        function(callback) {
            require(['app/views/show/S02TagRecommendation'], function(S02TagRecommendation) {
                callback(null, S02TagRecommendation);
            });
        },
        function(callback) {
            TemplateManager.load('menu.html', callback);
        }], function(err, results) {
            var S02TagRecommendation = results[0];
            var content$ = results[1];
            this._dom$.append(content$);

            $('li').on('click', function() {
                appRuntime.view.to(S02TagRecommendation);
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
