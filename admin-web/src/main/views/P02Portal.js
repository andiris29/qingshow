// @formatter:off
define([
    'main/views/View'
], function(
    View
) {
    violet.ui.factory.registerDependencies('main/views/P02Portal', [
        'main/views/components/p02/TradeStatusLi', 
        'main/views/components/p02/ItemCategoryLi',
        'main/views/components/p02/ItemPriceChangedLi',
        'main/views/components/p02/BonusSummaryLi']);
// @formatter:on
    var P02Portal = function(dom, initOptions) {
        P02Portal.superclass.constructor.apply(this, arguments);

        this._initManagerTrades();
        this._initManagerItems();
    };

    P02Portal.prototype._initManagerTrades = function() {
        var ul$ = $('#managerTrades', this._dom);
        var module = 'main/views/components/p02/TradeStatusLi';

        [2, 3, 7, 9, 10, 15, 17].forEach( function(status) {
            violet.ui.factory.createUi(module, {
                'status' : status
            }, ul$, this);
        }.bind(this));

        violet.ui.factory.createUi('main/views/components/p02/BonusSummaryLi', {
        }, ul$, this);
    };

    P02Portal.prototype._initManagerItems = function() {
        var ul$ = $('#managerItems', this._dom);
        var module = 'main/views/components/p02/ItemCategoryLi';

        this.request('/matcher/queryCategories', 'get', null, function(err, metadata, data) {
            var categories = data.categories.filter(function(category) {
                return category.parentRef !== undefined;
            });
            _.sortBy(categories, 'parentRef').forEach( function(category) {
                violet.ui.factory.createUi(module, {
                    'category' : category,
                    'parentRef' : _.find(data.categories, function(findingCategory) {
                        return category.parentRef === findingCategory._id;
                    })
                }, ul$, this);
            }.bind(this));
        }.bind(this));
    };

    violet.oo.extend(P02Portal, View);

    return P02Portal;
});
