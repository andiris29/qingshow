var util = require('util');

var ServerError = function (errorCode) {
    Error.call(this, 'server error');
    this.errorCode = errorCode;
    this.description = _codeToString(errorCode);
}

util.inherits(ServerError, Error);

//ErrorCode
ServerError.ServerError = 1000;
ServerError.IncorrectMailOrPassword = 1001;
ServerError.SessionExpired = 1002;
ServerError.ShowNotExist = 1003;
ServerError.ItemNotExist = 1004;
ServerError.PeopleNotExist = 1005;
ServerError.BrandNotExist = 1006;
ServerError.InvalidEmail = 1007;
ServerError.NotEnoughParam = 1008;
ServerError.PagingNotExist = 1009;
ServerError.EmailAlreadyExist = 1010;
ServerError.AlreadyLikeShow = 1011;
ServerError.NeedLogin = 1012;

var _codeToString = function (code) {
    switch (code) {
        case 1000 : return "ServerError";
        case 1001 : return "IncorrectMailOrPassword";
        case 1002 : return "SessionExpired";
        case 1003 : return "ShowNotExist";
        case 1004 : return "ItemNotExist";
        case 1005 : return "PeopleNotExist";
        case 1006 : return "BrandNotExist";
        case 1007 : return "InvalidEmail";
        case 1008 : return "NotEnoughParam";
        case 1009 : return "PagingNotExist";
        case 1010 : return "EmailAlreadyExist";
        case 1011 : return "AlreadyLikeShow";
        case 1012 : return "NeedLogin";

    }
}

module.exports = ServerError;