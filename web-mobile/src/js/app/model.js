// @formatter:off
define([
    'app/services/SerializationService'
], function(SerializationService) {
// @formatter:on
    /**
     *
     */
    var Model = function() {
        this._trigger$ = $({});

        this._user = null;
    };

    Model.prototype.on = function() {
        this._trigger$.on.apply(this._trigger$, arguments);
    };

    Model.prototype.trigger = function() {
        this._trigger$.trigger.apply(this._trigger$, arguments);
    };

    Model.prototype.user = function(value) {
        if (arguments.length > 0) {
            this._user = value;

            SerializationService.serializeLoginUser(this._user);
            return this;
        } else {
            return this._user;
        }
    };

    return new Model();
});
