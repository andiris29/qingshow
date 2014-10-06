// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/services/DataService'
], function(UIComponent, TemplateManager, DataService) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    // TODO Move to S03Show
    var ItemMain = function(dom) {
        ItemMain.superclass.constructor.apply(this, arguments);

        async.parallel([ function(callback) {
            // Load template
            TemplateManager.load('item.html', true, function(err, content$) {
                this._dom$.append(content$);
                callback(null);
            }.bind(this));
        }.bind(this), function(callback) {
            // Load template
            TemplateManager.load('item-main.html', true, function(err, content$) {
                callback(null, content$);
            }.bind(this));
        }.bind(this), function(callback) {
            // Load data
            DataService.request('/item', null, callback);
        }.bind(this)], function(err, results) {
            var main$ = results[1];
            main$.appendTo($('.qsItemMain', this._dom$));

            this._render(results[2]);
        }.bind(this));

    };
    andrea.oo.extend(ItemMain, UIComponent);

    ItemMain.prototype._render = function(itemJSON) {

        videojs($('.qsItemVideo', this._dom$).get(0));
    };

    return ItemMain;
});
