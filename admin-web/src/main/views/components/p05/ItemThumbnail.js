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
        
        $('#thumbnail', this._dom).css('background-image', violet.string.substitute('url({0})', item.thumbnail));
        
        $('#name', this._dom).text(item.name).attr('title', item.name);
        $('#_id', this._dom).text(item._id).attr('title', item._id);
    };
    violet.oo.extend(ItemThumbnail, violet.ui.UIBase);

    return ItemThumbnail;
});
