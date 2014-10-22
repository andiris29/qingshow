// @formatter:off
define([
    'app/services/UserService'
], function(UserService) {
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
            return this;
        } else {
            return this._user;
        }
    };

    var _SERIALIZATION_METADATA = {
        'version' : '1.0.0',
        'key' : 'com.focosee.qingshow'
    };

    Model.prototype.serialize = function() {
        if (window.localStorage) {
            var json = {
                'version' : _SERIALIZATION_METADATA.version
            };
            if (this._user) {
                $.extend(json, {
                    'userInfo' : {
                        'mail' : this._user.userInfo.mail,
                        'encryptedPassword' : this._user.userInfo.encryptedPassword
                    }
                });
            }
            window.localStorage[_SERIALIZATION_METADATA.key] = JSON.stringify(json);
        }
    };

    Model.prototype.deserialize = function(callback) {
        var tasks = [];
        if (window.localStorage) {
            var json = window.localStorage[_SERIALIZATION_METADATA.key];
            if (json) {
                json = JSON.parse(json);
                if (json.userInfo) {
                    tasks.push( function(callback) {
                        UserService.loginByEncryptedPassword(json.userInfo.mail, json.userInfo.encryptedPassword, function(metadata, data) {
                            this.user(data);
                            callback(null);
                        }.bind(this));
                    }.bind(this));
                }
            }
        }
        async.parallel(tasks, function(err, results) {
            callback();
        });
    };

    return new Model();
});
