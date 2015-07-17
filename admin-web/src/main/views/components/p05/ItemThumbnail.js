// @formatter:off
define([
    'main/services/codeMongoService'
], function(
    codeMongoService
) {
// @formatter:on
    var ItemThumbnail = function(dom, initOptions) {
        ItemThumbnail.superclass.constructor.apply(this, arguments);

        var item = initOptions.item;
        $('#name', this._dom).text(item.name);
        $('#thumbnail', this._dom).css('background-image', violet.string.substitute('url({0})', item.thumbnail));
    };
    violet.oo.extend(ItemThumbnail, violet.ui.UIBase);

    return ItemThumbnail;
});
