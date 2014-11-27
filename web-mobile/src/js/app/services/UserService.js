// @formatter:off
define([
    'app/services/DataService'
], function(DataService) {
// @formatter:on
    /**
     */
    var UserService = {};

    UserService.get = function(callback) {
        DataService.request('GET', '/user/get', null, callback);
    };

    UserService.login = function(id, password, callback) {
        DataService.request('POST', '/user/login', {
            'id' : id,
            'password' : password
        }, callback);
    };

    UserService.register = function(id, password, callback) {
        DataService.request('POST', '/user/register', {
            'id' : id,
            'password' : password
        }, callback);
    };

    UserService.update = function(updated, callback) {
        if (updated.password) {
            updated.password = updated.password;
            delete updated.password;
        }
        DataService.request('POST', '/user/update', updated, callback);
    };

    UserService.updatePortrait = function() {
        DataService.upload('/user/updatePortrait');
    };

    UserService.updateBackground = function() {
        DataService.upload('/user/updateBackground');
    };

    UserService.logout = function(callback) {
        DataService.request('POST', '/user/logout', {}, DataService.injectBeforeCallback(callback, function(metadata, data, model) {
            model.user(null);
        }));
    };

    return UserService;
});
