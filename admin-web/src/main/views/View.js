// @formatter:off
define([
    'main/core/UI',
    'main/services/navigationService',
    'main/services/httpService'
], function(
    UI,
    navigationService, 
    httpService
) {
// @formatter:on
    var View = function(dom, initOptions) {
        View.superclass.constructor.apply(this, arguments);
    };
    violet.oo.extend(View, UI);
    
    // ------ NavigationService ------
    View.prototype.pushView = function(id) {
    };

    View.prototype.popView = function() {
    };

    // ------ HTTPService ------
    View.prototype.request = function() {
        httpService.request.apply(null, arguments);
    };

    return View;
});
