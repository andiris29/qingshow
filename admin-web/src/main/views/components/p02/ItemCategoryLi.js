// @formatter:off
define([
    'main/services/codeMongoService'
], function(
    codeMongoService
) {
// @formatter:on
    var ItemCategoryLi = function(dom, initOptions) {
        ItemCategoryLi.superclass.constructor.apply(this, arguments);

        var category = initOptions.category,
            parentRef = initOptions.parentRef;
        var text = parentRef.name + '－' + category.name;
        $('#anchor', this._dom).text(text).on('click', function() {
            // TODO
        }.bind(this));
    };
    violet.oo.extend(ItemCategoryLi, violet.ui.UIBase);

    return ItemCategoryLi;
});
