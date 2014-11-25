// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/services/DataService',
    'app/utils/CodeUtils',
    'app/utils/RenderUtils'
], function(UIComponent, TemplateManager, DataService, CodeUtils, RenderUtils) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var Item = function(dom, data) {
        Item.superclass.constructor.apply(this, arguments);
        this._items = data.items;
        this._index = data.index;

        TemplateManager.load('show/item.html', function(err, content$) {
            this._dom$.append(content$);
            this._render();
        }.bind(this));
    };
    andrea.oo.extend(Item, UIComponent);

    Item.prototype._render = function() {
        Item.superclass._render.apply(this, arguments);

        // Slick covers
        var itemCoversContainer$ = $('.qsSlickItemCovers', this._dom$), slickItemTplt$;

        for (var i = 0; i < this._items.length; i++) {
            var item = this._items[(this._index + i) % this._items.length];
            if (i === 0) {
                var slickItem$ = slickItemTplt$ = $('.clone', itemCoversContainer$);
                this._renderInfo(item);
            } else {
                slickItem$ = slickItemTplt$.clone().appendTo(itemCoversContainer$);
            }
            slickItem$.height(itemCoversContainer$.height()).css('background-image', RenderUtils.imagePathToBackground(item.cover));
        }
        itemCoversContainer$.slick({
            'dots' : true,
            'arrows' : false,
            'slidesToShow' : 1,
            'slidesToScroll' : 1,
            'onAfterChange' : function(slicker, index) {
                this._renderInfo(this._items[index]);
            }.bind(this)
        });
        // Event
        itemCoversContainer$.on('click', function(event) {
            appRuntime.popup.remove(this);
        }.bind(this));
        $('.qsClose', this._dom$).on('click', function(event) {
            appRuntime.popup.remove(this);
        }.bind(this));
    };

    Item.prototype._renderInfo = function(item) {
        $('.qsName', this._dom$).text(item.name);
        if (item.brandRef) {
            $('.qsBrand', this._dom$).text(item.brandRef.name);
        } else {
            $('.qsBrand', this._dom$).hide();
        }
        if (item.source) {
            $('.qsBuy', this._dom$).on(appRuntime.events.click, function() {
                window.open(pitem.source, '_blank');
            });
        } else {
            $('.qsBuy', this._dom$).hide();
        }
    };

    return Item;
});
