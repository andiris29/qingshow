// @formatter:off
define([
], function() {
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

    NavigationService.prototype.push = function(module, initOptions) {
        violet.ui.factory.createViewAsync(module, initOptions, this._root$, function(err, view) {
            this._push(view);
        }.bind(this));
    };

    NavigationService.prototype.pop = function() {
        this._currentView.destroy();
        this._views.pop();

        this._currentView = this._views[this._views.length - 1];
        this._currentView.show();
    };

    NavigationService.prototype.popAll = function() {
        while (this._views.length > 1) {
            var view = this._views[this._views.length - 1];
            view.destroy();
            this._views.pop();
        }

        this._currentView = this._views[0];
        this._currentView.show();
    };

    NavigationService.prototype._push = function(view) {
        if (this._currentView) {
            this._currentView.hide();
        }
        this._currentView = view;
        this._views.push(view);
    };

    return new NavigationService();
});
