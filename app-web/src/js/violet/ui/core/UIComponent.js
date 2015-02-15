define([], function() {
    var UIComponent = function(dom, data) {
        if (dom.get) {
            this._dom = dom.get(0);
            this._dom$ = dom;
        } else {
            this._dom = dom;
            this._dom$ = $(dom);
        }
        this._data = data;

        this._trigger$ = $({});
        this.$ = function(selector) {
            return $(selector, this._dom);
        };
        this._destroyed = false;
    };

    UIComponent.prototype.dom$ = function() {
        return this._dom$;
    };

    UIComponent.prototype.dom = function() {
        return this._dom;
    };

    // ------ Lifecyle ------
    UIComponent.prototype.destroy = function() {
        this.trigger('destroying');

        this._dom$.remove();
        this.$ = null;
        this._destroyed = true;

        this.trigger('destroy');
        this.off();
    };

    // ------ Event ------
    UIComponent.prototype.on = function() {
        this._trigger$.on.apply(this._trigger$, arguments);
    };

    UIComponent.prototype.off = function() {
        this._trigger$.off.apply(this._trigger$, arguments);
    };

    UIComponent.prototype.trigger = function() {
        this._trigger$.trigger.apply(this._trigger$, arguments);
    };

    // ------ Measure ------
    UIComponent.prototype.getPreferredSize = function() {
    };

    return UIComponent;
});
