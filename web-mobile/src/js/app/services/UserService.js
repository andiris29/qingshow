// @formatter:off
define([
    'app/services/DataService'
], function(DataService) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var UserService = {};

    // CryptoJS.DES.decrypt(encrypted, key).toString(CryptoJS.enc.Utf8);
    var _key = CryptoJS.enc.Hex.parse('qsPasswordKey');
    var _cfg = {
        'iv' : CryptoJS.enc.Hex.parse('qsPasswordIV')
    };

    UserService.login = function(mail, password, callback) {
        DataService.request('POST', '/user/login', {
            'mail' : mail,
            'encryptedPassword' : CryptoJS.DES.encrypt(password, _key, _cfg).toString()
        }, callback);
    };
    UserService.loginByEncryptedPassword = function(mail, encryptedPassword, callback) {
        DataService.request('POST', '/user/login', {
            'mail' : mail,
            'encryptedPassword' : encryptedPassword
        }, callback);
    };

    UserService.register = function(mail, password, callback) {
        DataService.request('POST', '/user/register', {
            'mail' : mail,
            'encryptedPassword' : CryptoJS.DES.encrypt(password, _key, _cfg).toString()
        }, callback);
    };

    UserService.update = function(updated, callback) {
        if (updated.password) {
            updated.encryptedPassword = CryptoJS.DES.encrypt(updated.password, _key, _cfg).toString();
            delete updated.password;
        }
        DataService.request('POST', '/user/update', updated, callback);

    };

    UserService.logout = function(callback) {
        DataService.request('POST', '/user/logout', {}, callback);
    }

    UserService.encrypt = function(value) {
        return CryptoJS.DES.encrypt(value, _key, _cfg).toString();
    }

    return UserService;
});
