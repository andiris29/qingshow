var util = require('util');
var winston = require('winston');

var ServerError = function(errorCode, description, err) {
    Error.call(this, 'server error');
    this.errorCode = errorCode;
    this.description = description || _codeToString(errorCode);
    this.domain = 'ServerError';
    if (errorCode === 1000) {
        err = err || new Error();
        this.stack = err.stack;
        winston.error(new Date().toString() + '- ServerError: ' + this.errorCode);
        winston.error('\t' + this.stack);
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
    'IncorrectMailOrPassword' : new ServerError(1001, 'IncorrectMailOrPassword'),
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
    'NeedLogin' : new ServerError(1012, 'NeedLogin'),
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
    'MobileAlreadyExist' : new ServerError(1029, 'MobileAlreadyExist'),
    'SMSValidationFail' : new ServerError(1030, 'SMSValidationFail'),
    'SMSlimitedSend' : new ServerError(1031, 'SMSlimitedSend'),
    'SMSFrequentlyRequest' : new ServerError(1032, 'SMSFrequentlyRequest')
};
