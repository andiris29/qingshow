// @formatter:off
define([
    'app/services/UserService'
], function(UserService) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var SerializationService = {};

    var _SERIALIZATION_METADATA = {
        'version' : '1.0.0',
        'key' : 'com.focosee.qingshow'
    };
    var _getLocalStorage = function() {
        if (window.localStorage) {
            var json = window.localStorage[_SERIALIZATION_METADATA.key];
            try {
                return JSON.parse(json);
            } catch(err) {
            }
        }
    };
    var _setLocalStorage = function(json) {
        if (window.localStorage) {
            window.localStorage[_SERIALIZATION_METADATA.key] = JSON.stringify(json);
        }
    };

    SerializationService.serializeLoginUser = function(user) {
        var json = _getLocalStorage() || {
            'version' : _SERIALIZATION_METADATA.version
        };
        if (user) {
            $.extend(json, {
                'loginUser' : {
                    'userInfo' : {
                        'mail' : user.userInfo.mail,
                        'encryptedPassword' : user.userInfo.encryptedPassword
                    }
                }
            });
        } else {
            delete json.loginUser;
        }
        _setLocalStorage(json);
    };

    SerializationService.deserializeLoginUser = function(callback) {
        var json = _getLocalStorage();
        if (json && json.loginUser) {
            var userInfo = json.loginUser.userInfo;
            UserService.loginByEncryptedPassword(userInfo.mail, userInfo.encryptedPassword, function(metadata, data) {
                require(['app/model'], function(model) {
                    model.user(data.people);
                    callback();
                });
            });
        } else {
            callback();
        }
    };

    return SerializationService;
});
