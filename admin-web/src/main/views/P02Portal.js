// @formatter:off
define([
    'main/views/View'
], function(
    View
) {
// @formatter:on
    var P02Portal = function(dom, initOptions) {
        P02Portal.superclass.constructor.apply(this, arguments);

        this._initManagerTrades();
        this._initManagerItems();
    };

    P02Portal.prototype._initManagerTrades = function() {
        var ul$ = $('#managerTrades', this._dom);
        var module = 'main/views/components/p02/TradeStatusLi';

        violet.ui.factory.load(module, function() {
            [1, 2, 7, 11, 16].forEach(function(status) {
                violet.ui.factory.createUi(module, {
                    'status' : status
                }, ul$, this);
            }.bind(this));
        }.bind(this));
    };

    P02Portal.prototype._initManagerItems = function() {
        var ul$ = $('#managerItems', this._dom);
        var module = 'main/views/components/p02/ItemCategoryLi';

        this.request('/matcher/queryCategories', 'get', null, function(err, metadata, data) {
            violet.ui.factory.load(module, function() {
                var categories = data.categories.filter(function(category) {
                    return category.parentRef !== undefined;
                });
                _.sortBy(categories, 'parentRef').forEach(function(category) {
                    violet.ui.factory.createUi(module, {
                        'category' : category,
                        'parentRef' : _.find(data.categories, function(findingCategory) {
                            return category.parentRef === findingCategory._id;
                        })
                    }, ul$, this);
                }.bind(this));
            }.bind(this));
        }.bind(this));
    };

    violet.oo.extend(P02Portal, View);

    return P02Portal;
});
