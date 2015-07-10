// @formatter:off
define([
    'main/services/navigationService',
    'main/services/httpService'
], function(navigationService, httpService) {
// @formatter:on
    var View = function(dom, initOptions) {
        if (dom.get) {
            this._dom = dom.get(0);
            this._dom$ = dom;
        } else {
            this._dom = dom;
            this._dom$ = $(dom);
        }
        this._initOptions = initOptions;

        this._trigger$ = $({});
        this.$ = function(selector) {
            return $(selector, this._dom);
        };
        this._destroyed = false;
    };

    View.prototype.dom$ = function() {
        return this._dom$;
    };

    View.prototype.dom = function() {
        return this._dom;
    };

    View.prototype.hide = function() {
        this._dom$.hide();
    };

    View.prototype.show = function() {
        this._dom$.show();
    };
    // ------ Lifecyle ------
    View.prototype.destroy = function() {
        this.trigger('destroying');

        this._dom$.empty();
        this.$ = null;
        this._destroyed = true;

        this.trigger('destroy');
        this.off();
    };

    // ------ Event ------
    View.prototype.on = function() {
        this._trigger$.on.apply(this._trigger$, arguments);
    };

    View.prototype.off = function() {
        this._trigger$.off.apply(this._trigger$, arguments);
    };

    View.prototype.trigger = function() {
        this._trigger$.trigger.apply(this._trigger$, arguments);
    };

    // ------ Measure ------
    View.prototype.getPreferredSize = function() {
    };

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
