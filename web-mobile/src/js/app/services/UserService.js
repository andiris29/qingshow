// @formatter:off
define([
    'app/services/DataService'
], function(DataService) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var UserService = {};

    // CryptoJS.AES.decrypt(encrypted, key).toString(CryptoJS.enc.Utf8);
    var _key = CryptoJS.enc.Hex.parse('qsPasswordKey');
    var _iv = {
        'iv' : CryptoJS.enc.Hex.parse('qsPasswordIV')
    };

    UserService.login = function(mail, password, callback) {
        DataService.request('POST', '/user/login', {
            'mail' : mail,
            'encryptedPassword' : CryptoJS.AES.encrypt(password, _key, _iv).toString()
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
            'encryptedPassword' : CryptoJS.AES.encrypt(password, _key, _iv).toString()
        }, callback);
    };

    UserService.update = function(updated, callback) {
        if (updated.password) {
            updated.encryptedPassword = CryptoJS.AES.encrypt(updated.password, _key).toString();
            delete updated.password;
        }
        DataService.request('POST', '/user/update', updated, callback);

    };

    return UserService;
});
