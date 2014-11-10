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

                for (var selector in _itemMetadata) {
                    if ($(selector, selected$).length) {
                        var metadata = _itemMetadata[selector];
                        appRuntime.view.to(metadata.clazz, $.extend({
                            'title' : $('.qsCategoryText', selected$).text()
                        }, metadata.data));
                        break;
                    }
                }
                // if ($('.qsHot', selected$).length) {
                // if ($('.qsPromotion', selected$).length) {
                // feeding = FeedingService.chosenByPromotion;
                // } else {
                // if (!$('.qsModels', selected$).length) {
                // feeding = FeedingService.hot;
                // }
                // }
                // } else if ($('.qsComparison', selected$).length) {
                // appRuntime.view.to('app/views/show/S03Comparison', {
                // 'feeding' : feeding,
                // 'title' : $('.qsCategoryText', selected$).text()
                // });
                // } else if ($('.qsBag', selected$).length) {
                // feeding = FeedingService.tagBag;
                // } else if ($('.qsAccessories', selected$).length) {
                // feeding = FeedingService.tagAccesories;
                // } else if ($('.qsShoe', selected$).length) {
                // feeding = FeedingService.tagShoe;
                // } else if ($('.qsCosmetic', selected$).length) {
                // feeding = FeedingService.studio;
                // }
                // if (feeding) {
                // appRuntime.view.to('app/views/show/S02Feeding', {
                // 'feeding' : feeding,
                // 'title' : $('.qsCategoryText', selected$).text()
                // });
                // }
                appRuntime.popup.remove(this);
            }.bind(this));
        }.bind(this));
    };

    andrea.oo.extend(Menu, UIComponent);

    var _itemMetadata = {
        '.qsHot.qsPromotion' : {
            'clazz' : 'app/views/show/S02Feeding',
            'data' : {
                'feeding' : FeedingService.chosenByPromotion
            }
        },
        '.qsHot:not(.qsModels)' : {
            'clazz' : 'app/views/show/S02Feeding',
            'data' : {
                'feeding' : FeedingService.hot
            }
        },
        '.qsComparison' : {
            'clazz' : 'app/views/show/S03Comparison',
            'data' : {
                // TODO
                'feeding' : FeedingService.chosenByEditor
            }
        },
        '.qsBag' : {
            'clazz' : 'app/views/show/S02Feeding',
            'data' : {
                'feeding' : FeedingService.tagBag
            }
        },
        '.qsAccessories' : {
            'clazz' : 'app/views/show/S02Feeding',
            'data' : {
                'feeding' : FeedingService.tagAccesories
            }
        },
        '.qsShoe' : {
            'clazz' : 'app/views/show/S02Feeding',
            'data' : {
                'feeding' : FeedingService.tagShoe
            }
        },
        '.qsCosmetic' : {
            'clazz' : 'app/views/show/S02Feeding',
            'data' : {
                'feeding' : FeedingService.studio
            }
        }
    };
    Menu.prototype.getPreferredSize = function() {
        return {
            'width' : 640,
            'height' : 510
        };
    };

    return Menu;
});
