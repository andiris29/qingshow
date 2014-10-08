define([], function() {
    var UIComponent = function(dom, data) {
        if (dom.get) {
            this._dom = dom.get(0);
            this._dom$ = dom;
        } else {
            this._dom = dom;
            this._dom$ = $(dom);
        }

        this._trigger$ = $({});
    };

    UIComponent.prototype.destroy = function() {
        this.trigger('destroying');
        this._dom$.remove();
        this.trigger('destroy');
        this._trigger$.off();
    };

    UIComponent.prototype.dom$ = function() {
        return this._dom$;
    };

    UIComponent.prototype.dom = function() {
        return this._dom;
    };

    UIComponent.prototype.hide = function() {
        this._dom$.hide();
    };

    UIComponent.prototype.show = function() {
        this._dom$.show();
    };

    UIComponent.prototype.on = function() {
        this._trigger$.on.apply(this._trigger$, arguments);
    };

    UIComponent.prototype.trigger = function() {
        this._trigger$.trigger.apply(this._trigger$, arguments);
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
        this.trigger('resize');
    };

    return UIComponent;
});
