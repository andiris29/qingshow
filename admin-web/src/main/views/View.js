// @formatter:off
define([
    'main/services/navigationService',
    'main/services/appService'
], function(
    navigationService, 
    appService
) {
// @formatter:on
    var View = function(dom, initOptions) {
        View.superclass.constructor.apply(this, arguments);

        this._header = null;
    };
    violet.oo.extend(View, violet.ui.ViewBase);

    // ------ Components ------
    View.prototype.header = function(value) {
        if (arguments.length) {
            this._header = value;
        } else {
            return this._header;
        }
    };
    // ------ NavigationService ------
    View.prototype.push = function() {
        navigationService.push.apply(navigationService, arguments);
    };
    View.prototype.pop = function() {
        navigationService.pop.apply(navigationService, arguments);
    };
    View.prototype.popAll = function() {
        navigationService.popAll.apply(navigationService, arguments);
    };
    // ------ HTTPService ------
    View.prototype.request = function() {
        appService.request.apply(null, arguments);
    };

    return View;
});
