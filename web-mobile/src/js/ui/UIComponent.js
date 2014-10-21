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
        this._destroyed = false;
        this._invalidate = false;
    };

    UIComponent.prototype.destroy = function() {
        this.trigger('destroying');
        this._dom$.remove();
        this.trigger('destroy');
        this._trigger$.off();
        this._destroyed = true;
    };

    UIComponent.prototype.destroyed = function() {
        return this._destroyed;
    };

    UIComponent.prototype.dom$ = function() {
        return this._dom$;
    };

    UIComponent.prototype.dom = function() {
        return this._dom;
    };

    UIComponent.prototype.on = function() {
        this._trigger$.on.apply(this._trigger$, arguments);
    };

    UIComponent.prototype.trigger = function() {
        this._trigger$.trigger.apply(this._trigger$, arguments);
    };

    UIComponent.prototype.invalidate = function() {
        if (!this._invalidate) {
            this._invalidate = true;
            setTimeout(this.validate.bind(this), 0);
        }
    };

    UIComponent.prototype.validate = function() {
    };

    UIComponent.prototype.getPreferredSize = function() {
    };

    UIComponent.prototype._render = function() {
        setTimeout(this._onAfterRender.bind(this), 0);
    };

    UIComponent.prototype._onAfterRender = function() {
        $('img', this._dom).on('load', function() {
            this.trigger('resize');
        }.bind(this));
        this.trigger('afterRender');

    };

    return UIComponent;
});
