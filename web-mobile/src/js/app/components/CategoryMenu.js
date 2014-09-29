// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/views/Category'
], function(UIComponent, TemplateManager, Category) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var CategoryMenu = function(dom) {
        CategoryMenu.superclass.constructor.apply(this, arguments);

        async.parallel([
        function(callback) {
            TemplateManager.load('category-menu.html', callback);
        }], function(err, results) {
            var content$ = results[0];
            this._dom$.append(content$);

            $('li').on('click', function() {
                appRuntime.view.to(Category);
                appRuntime.popup.remove(this);
            }.bind(this));
        }.bind(this));
    };

    andrea.oo.extend(CategoryMenu, UIComponent);

    CategoryMenu.prototype.getPreferredSize = function() {
        return {
            'width' : 640,
            'height' : 510
        };
    };

    return CategoryMenu;
});
