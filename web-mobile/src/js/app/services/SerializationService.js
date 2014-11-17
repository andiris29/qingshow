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

    SerializationService.serialize = function(key, value) {
        var json = _getLocalStorage() || {
            'version' : _SERIALIZATION_METADATA.version
        };
        if (value) {
            var object = {};
            object[key] = value;
            $.extend(json, object);
        } else {
            delete json[key];
        }
        _setLocalStorage(json);
    };

    SerializationService.deserialize = function(key) {
        var json = _getLocalStorage();
        if (json) {
            return json[key];
        }
    };

    return SerializationService;
});
