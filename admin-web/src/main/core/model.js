// @formatter:off
define([
], function() {
// @formatter:on
    var Model = function() {
        this._trigger$ = $({});
    };

    // ------ Event ------
    Model.prototype.on = function() {
        this._trigger$.on.apply(this._trigger$, arguments);
    };

    Model.prototype.one = function() {
        this._trigger$.one.apply(this._trigger$, arguments);
    };

    Model.prototype.off = function() {
        this._trigger$.off.apply(this._trigger$, arguments);
    };

    Model.prototype.trigger = function() {
        this._trigger$.trigger.apply(this._trigger$, arguments);
    };
    return new Model();
});
