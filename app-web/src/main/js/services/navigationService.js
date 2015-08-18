// @formatter:off
define([
], function() {
// @formatter:on
    /**
     * TODO Support animation
     */
    var navigationService = {};

    var _root = null,
        _root$ = null;
    var _views = [],
        _currentView = null;

    navigationService.config = function(value) {
        _root = value.root;
        _root$ = $(_root);
    };

    navigationService.push = function(module, initOptions) {
        violet.ui.factory.createViewAsync(module, initOptions, _root$, function(err, view) {
            _push(view);
        }.bind(this));
    };

    navigationService.pop = function() {
        _currentView.destroy();
        _views.pop();

        _currentView = _views[_views.length - 1];
        _currentView.show();
    };

    navigationService.popAll = function() {
        while (_views.length > 1) {
            var view = _views[_views.length - 1];
            view.destroy();
            _views.pop();
        }

        _currentView = _views[0];
        _currentView.show();
    };

    var _push = function(view) {
        if (_currentView) {
            _currentView.hide();
        }
        _currentView = view;
        _views.push(view);
    };

    return navigationService;
});
