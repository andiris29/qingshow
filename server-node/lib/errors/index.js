var util = require('util');

var ServerError = function(errorCode, description, err) {
    Error.call(this, 'server error');
    this.errorCode = errorCode;
    this.description = description || _codeToString(errorCode);
    this.domain = 'ServerError';
    if (errorCode === 1000) {
        err = err || new Error();
        this.stack = err.stack;
        require('../runtime/loggers').get('caught-exceptions').error({
            'errorCode' : this.errorCode,
            'stack' : (this.stack || '').split('\n')
        });
    }
};

util.inherits(ServerError, Error);

module.exports = {
    'genUnkownError' : function(err) {
        if ( err instanceof Error) {
            return new ServerError(1000, 'UnkownError', err);
        } else {
            return new ServerError(1000, err || 'UnkownError');
        }
    },
    // TODO
    'genGoblin' : function(message, err) {
        return new ServerError(1028, message, err);
    },
    'ERR_INCORRECT_PASSWORD' : new ServerError(1001, 'ERR_INCORRECT_PASSWORD'),
    'SessionExpired' : new ServerError(1002, 'SessionExpired'),
    'ShowNotExist' : new ServerError(1003, 'ShowNotExist'),
    'ItemNotExist' : new ServerError(1004, 'ItemNotExist'),
    'PeopleNotExist' : new ServerError(1005, 'PeopleNotExist'),
    'BrandNotExist' : new ServerError(1006, 'BrandNotExist'),
    'InvalidEmail' : new ServerError(1007, 'InvalidEmail'),
    'NotEnoughParam' : new ServerError(1008, 'NotEnoughParam'),
    'PagingNotExist' : new ServerError(1009, 'PagingNotExist'),
    'EmailAlreadyExist' : new ServerError(1010, 'EmailAlreadyExist'),
    'AlreadyLikeShow' : new ServerError(1011, 'AlreadyLikeShow'),
    'ERR_NOT_LOGGED_IN' : new ServerError(1012, 'ERR_NOT_LOGGED_IN'),
    'PItemNotExist' : new ServerError(1017, 'PItemNotExist'),
    'RequestValidationFail' : new ServerError(1018, 'RequestValidationFail'),
    'AlreadyRelated' : new ServerError(1019, 'AlreadyRelated'),
    'AlreadyUnrelated' : new ServerError(1020, 'AlreadyUnrelated'),
    'InvalidCurrentPassword' : new ServerError(1021, 'InvalidCurrentPassword'),
    'IsNotAdmin' : new ServerError(1022, 'IsNotAdmin'),
    'TopShopNotExist' : new ServerError(1023, 'TopShopNotExist'),
    'TradeNotExist' : new ServerError(1024, 'TradeNotExist'),
    'TradeStatusChangeError' : new ServerError(1025, 'TradeStatusChangeError'),
    'AlreadyLaunched' : new ServerError(1026, 'AlreadyLaunched'),
    'UnsupportVersion' : new ServerError(1027, 'UnsupportVersion'),
    'GoblinError' : new ServerError(1028, 'GoblinError'),
    'ERR_MOBILE_ALREADY_REGISTERED' : new ServerError(1029, 'ERR_MOBILE_ALREADY_REGISTERED'),
    'SMSValidationFail' : new ServerError(1030, 'SMSValidationFail'),
    'ERR_SMS_LIMIT_EXCEEDED' : new ServerError(1031, 'ERR_SMS_LIMIT_EXCEEDED'),
    'FrequentlyRequest' : new ServerError(1032, 'FrequentlyRequest'),
    'NickNameAlreadyExist' : new ServerError(1033, 'NickNameAlreadyExist'),
    'ERR_INVALID_ITEM' : new ServerError(1034, 'ERR_INVALID_ITEM'),
    'GoblinSlaveDisabled' : new ServerError(1035, 'GoblinSlaveDisabled'),
    'AlreadyLoggedIn' : new ServerError(1036, 'AlreadyLoggedIn'),
    'ERR_PERMISSION_DENIED' : new ServerError(1037, 'ERR_PERMISSION_DENIED'),
    'INVALID_OBJECT_ID' : new ServerError(1038, 'INVALID_OBJECT_ID'),
    'ERR_WEIXIN_ALREADY_REGISTERED' : new ServerError(1039, 'ERR_WEIXIN_ALREADY_REGISTERED'),
    'INVALID_SHARED_OBJECT' : new ServerError(1040, 'INVALID_SHARED_OBJECT'),
    'ERR_WEIXIN_NOT_BOUND' : new ServerError(1041, 'ERR_WEIXIN_NOT_BOUND'),
    'ERR_SEND_WEIXIN_RED_PACK_FAILED' : new ServerError(1042, 'ERR_SEND_WEIXIN_RED_PACK_FAILED'),
    'ERR_EXCEED_CREATE_SHOW_LIMIT' : new ServerError(1043, 'ERR_EXCEED_CREATE_SHOW_LIMIT'),
    'ERR_INVALID_TRADE' : new ServerError(1035, 'ERR_INVALID_TRADE')
};
