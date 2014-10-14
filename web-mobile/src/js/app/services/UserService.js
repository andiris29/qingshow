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
    var _key = 'qsPasswordKey';

    UserService.login = function(mail, password, callback) {
        DataService.request('/user/login', {
            'mail' : mail,
            'encryptedPassword' : CryptoJS.AES.encrypt(password, _key).toString()
        }, callback);
    };

    return UserService;
});
