// @formatter:off
define([
], function(
) {
// @formatter:on
    var P01NotFound = function(dom, initOptions) {
        P01NotFound.superclass.constructor.apply(this, arguments);
        
        this._dom$.text('404: ' + window.location.href);
    };
    violet.oo.extend(P01NotFound, violet.ui.ViewBase);

    return P01NotFound;
});
