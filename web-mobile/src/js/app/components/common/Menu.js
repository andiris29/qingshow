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
                    feeding = FeedingService.hot;
                } else if ($('.qsWomen', selected$).length) {
                    feeding = function(pageNo, callback) {
                        FeedingService.byTag('women', pageNo, callback);
                    };
                } else if ($('.qsBag', selected$).length) {
                    feeding = function(pageNo, callback) {
                        FeedingService.byTag('bag', pageNo, callback);
                    };
                } else if ($('.qsAccessories', selected$).length) {
                    feeding = function(pageNo, callback) {
                        FeedingService.byTag('accessories', pageNo, callback);
                    };
                } else if ($('.qsShoe', selected$).length) {
                    feeding = function(pageNo, callback) {
                        FeedingService.byTag('shoe', pageNo, callback);
                    };
                } else {
                    // TODO 最美搭配, 人气模特, 设计风尚, 品牌专区
                    feeding = FeedingService.hot;
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
