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
    var Show = function(dom) {
        Show.superclass.constructor.apply(this, arguments);

        async.parallel([ function(callback) {
            // Load template
            TemplateManager.load('show/show.html', true, function(err, content$) {
                this._dom$.append(content$);
                callback(null);
            }.bind(this));
        }.bind(this), function(callback) {
            // Load template
            TemplateManager.load('show/show-main.html', true, function(err, content$) {
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
    andrea.oo.extend(Show, UIComponent);

    Show.prototype._render = function(itemJSON) {
        Show.superclass._render.apply(this, arguments);
        
        videojs($('.qsItemVideo', this._dom$).get(0));
    };

    return Show;
});
