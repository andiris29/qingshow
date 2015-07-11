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
    };
    violet.oo.extend(View, violet.ui.ViewBase);
    
    // ------ NavigationService ------
    View.prototype.push = function(module) {
        navigationService.push(module);
    };

    View.prototype.pop = function() {
    };

    // ------ HTTPService ------
    View.prototype.request = function() {
        appService.request.apply(null, arguments);
    };

    return View;
});
