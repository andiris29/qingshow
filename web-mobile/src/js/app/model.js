// @formatter:off
define([
], function() {
// @formatter:on
    /**
     *
     */
    var Model = function() {
        this._trigger$ = $({});

        this._user = null;
        this._showsLookup = {};
    };

    Model.prototype.on = function() {
        this._trigger$.on.apply(this._trigger$, arguments);
    };

    Model.prototype.trigger = function() {
        this._trigger$.trigger.apply(this._trigger$, arguments);
    };

    Model.prototype.user = function(value) {
        if (arguments.length > 0) {
            var changed = (value && !this._user) || (!value && this._user) || (value._id !== this._user._id);
            this._user = value;
            if (changed) {
                this.trigger('userChanged');
            }
            return this;
        } else {
            return this._user;
        }
    };

    return new Model();
});
