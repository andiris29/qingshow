// @formatter:off
define([
    'main/views/View'
], function(
    View
) {
    violet.ui.factory.registerDependencies('main/views/P02Portal', [
        'main/views/components/p02/TradeStatusLi', 
        'main/views/components/p02/ItemCategoryLi', 
        'main/views/components/p02/NewRecommandationsLi',
        'main/views/components/p02/QuestSharingObjectiveCompleteLi']);
// @formatter:on
    var P02Portal = function(dom, initOptions) {
        P02Portal.superclass.constructor.apply(this, arguments);

        this._initManagerTrades();
        this._initManagerItems();
        this._initManagerPush();
    };

    P02Portal.prototype._initManagerTrades = function() {
        var ul$ = $('#managerTrades', this._dom);
        var module = 'main/views/components/p02/TradeStatusLi';

        [0, 1, 2, 3, 5, 7, 9, 10, 11, 12, 13, 14, 15, 16, 17].forEach( function(status) {
            violet.ui.factory.createUi(module, {
                'status' : status
            }, ul$, this);
        }.bind(this));
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

    P02Portal.prototype._initManagerPush = function() {
        var ul$ = $('#managerPush', this._dom);

        ['A1', 'A2', 'A3', 'A4'].forEach( function(group) {
            violet.ui.factory.createUi('main/views/components/p02/NewRecommandationsLi', {
                'group' : group
            }, ul$, this);
        }.bind(this));

        violet.ui.factory.createUi('main/views/components/p02/QuestSharingObjectiveCompleteLi', {
        }, ul$, this);
    };

    violet.oo.extend(P02Portal, View);

    return P02Portal;
});
