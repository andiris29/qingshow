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

    UserService.login = function(mail, password, callback) {
        DataService.request('POST', '/user/login', {
            'mail' : mail,
            'password' : password
        }, callback);
    };

    UserService.register = function(mail, password, callback) {
        DataService.request('POST', '/user/register', {
            'mail' : mail,
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

    UserService.logout = function(callback) {
        DataService.request('POST', '/user/logout', {}, callback);
    };

    return UserService;
});
