// @formatter:off
define([
    'main/services/uiService'
], function(uiService) {
// @formatter:on
    /**
     * TODO Support animation
     */
    var NavigationService = function() {
        this._root = null;
        this._root$ = null;

        this._views = [];
        this._currentView = null;
    };

    NavigationService.prototype.root = function(value) {
        this._root = value;
        this._root$ = $(this._root);
    };

    NavigationService.prototype.push = function(module) {
        uiService.loadView(module, function(err, view) {
            this._push(view);
        }.bind(this));
    };

    NavigationService.prototype.pop = function() {

    };

    NavigationService.prototype._push = function(view) {
        if (this._currentView) {
            this._currentView.hide();
        }
        this._root$.append(view.dom());
        this._currentView = view;
    };

    return new NavigationService();
});
