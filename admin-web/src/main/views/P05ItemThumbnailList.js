// @formatter:off
define([
    'main/views/View',
    'main/services/codeMongoService'
], function(
    View,
    codeMongoService
) {
    violet.ui.factory.registerDependencies('main/views/P05ItemThumbnailList', [
        'main/views/components/p05/ItemThumbnail']);
// @formatter:on

    var P05ItemThumbnailList = function(dom, initOptions) {
        P05ItemThumbnailList.superclass.constructor.apply(this, arguments);

        var category = initOptions.category,
            parentRef = initOptions.parentRef;

        this.request('/admin/find', 'get', {
            'collection' : 'items',
            'pageSize' : 1000,
            'categoryRef' : category._id
        }, function(err, metadata, data) {
            if (err || metadata.error) {
                alertify.error(violet.string.substitute('不存在分类为{0}的商品', category.name));
                return;
            }
            var parent$ = $('ul', this._dom);
            data.models.filter(function(item){
                category.matchInfo = category.matchInfo || {};
                if(category.matchInfo.excludeDelistBefore){
                    return item.delist ? item.delist > category.matchInfo.excludeDelistBefore : true;
                }else {
                    return !item.delist;
                }
            }).forEach( function(item) {
                violet.ui.factory.createUi('main/views/components/p05/ItemThumbnail', {
                    'item' : item
                }, parent$, this);
            }.bind(this));
        }.bind(this));
    };

    violet.oo.extend(P05ItemThumbnailList, View);

    P05ItemThumbnailList.prototype.header = function(value) {
        P05ItemThumbnailList.superclass.header.apply(this, arguments);
        if (value) {
            var category = this._initOptions.category,
                parentRef = this._initOptions.parentRef;
            this._header.title(violet.string.substitute('{0}：{1}', parentRef.name, category.name));
        }
    };

    return P05ItemThumbnailList;
});
