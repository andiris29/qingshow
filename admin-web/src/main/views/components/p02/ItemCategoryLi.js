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
        var text = violet.string.substitute('商品管理－{0}：{1}', parentRef.name, category.name);
        $('#anchor', this._dom).text(text).on('click', function() {
            // TODO
        }.bind(this));
    };
    violet.oo.extend(ItemCategoryLi, violet.ui.UIBase);

    return ItemCategoryLi;
});
