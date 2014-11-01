// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/services/FeedingService'
], function(UIComponent, TemplateManager, FeedingService) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var Menu = function(dom) {
        Menu.superclass.constructor.apply(this, arguments);

        TemplateManager.load('common/menu.html', function(err, content$) {
            this._dom$.append(content$);

            $('li').on(appRuntime.events.click, function(event) {
                var selected$ = $(event.currentTarget), feeding;

                if ($('.qsHot', selected$).length) {
                    if ($('.qsPromotion', selected$).length) {
                        feeding = FeedingService.chosenByPromotion;
                    } else {
                        if (!$('.qsModels', selected$).length) {
                            feeding = FeedingService.hot;
                        }
                    }
                } else if ($('.qsComparison', selected$).length) {
                    // TODO
                } else if ($('.qsBag', selected$).length) {
                    feeding = FeedingService.tagBag;
                } else if ($('.qsAccessories', selected$).length) {
                    feeding = FeedingService.tagAccesories;
                } else if ($('.qsShoe', selected$).length) {
                    feeding = FeedingService.tagShoe;
                } else if ($('.qsCosmetic', selected$).length) {
                    feeding = FeedingService.studio;
                }
                if (feeding) {
                    appRuntime.view.to('app/views/show/S02Feeding', {
                        'feeding' : feeding,
                        'title' : $('.qsCategoryText', selected$).text()
                    });
                }
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
