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
        this._item = data;

        TemplateManager.load('show/item.html', function(err, content$) {
            this._dom$.append(content$);
            this._render();
        }.bind(this));
    };
    andrea.oo.extend(Item, UIComponent);

    Item.prototype._render = function() {
        Item.superclass._render.apply(this, arguments);

        var item = this._item;
        $('.qsCover', this._dom$).css('background-image', RenderUtils.imagePathToBackground(item.cover));
        $('.qsName', this._dom$).text(item.name);
        if (item.brandRef) {
            $('.qsBrand', this._dom$).text(item.brandRef.name);
        } else {
            $('.qsBrand', this._dom$).hide();
        }
        if (item.source) {
            $('.qsBuy', this._dom$).on(appRuntime.events.click, function() {
                window.location = item.source;
            });
        } else {
            $('.qsBuy', this._dom$).hide();
        }

        $('.qsCloseHitArea', this._dom$).on(appRuntime.events.click, function(event) {
            appRuntime.popup.remove(this);
        }.bind(this));
    };

    return Item;
});
