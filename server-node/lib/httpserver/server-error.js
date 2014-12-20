var util = require('util');

var ServerError = function(errorCode, description, err) {
    Error.call(this, 'server error');
    this.errorCode = errorCode;
    this.description = description || _codeToString(errorCode);
    if (errorCode === ServerError.ServerError) {
        err = err || new Error();
        this.stack = err.stack;
        console.log(new Date().toString() + ': ServerError');
        console.log('\t' + this.stack);
    }
};

ServerError.fromCode = function(code) {
    return new ServerError(code);
};
ServerError.fromDescription = function(description) {
    return new ServerError(ServerError.ServerError, description, new Error());
};
ServerError.fromError = function(err) {
    return new ServerError(ServerError.ServerError, 'Server Error: ' + err.toString(), err);
};

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
ServerError.AlreadyFollowPeople = 1013;
ServerError.DidNotFollowPeople = 1014;
ServerError.AlreadyFollowBrand = 1015;
ServerError.DidNotFollowBrand = 1016;
ServerError.PItemNotExist = 1017;
ServerError.RequestValidationFail = 1018;
ServerError.AlreadyRelated = 1019;
ServerError.AlreadyUnrelated = 1020;
ServerError.InvalidCurrentPassword = 1021;
ServerError.IsNotAdmin = 1022;

var _codeToString = function(code) {
    switch (code) {
        case 1000 :
            return "ServerError";
        case 1001 :
            return "IncorrectMailOrPassword";
        case 1002 :
            return "SessionExpired";
        case 1003 :
            return "ShowNotExist";
        case 1004 :
            return "ItemNotExist";
        case 1005 :
            return "PeopleNotExist";
        case 1006 :
            return "BrandNotExist";
        case 1007 :
            return "InvalidEmail";
        case 1008 :
            return "NotEnoughParam";
        case 1009 :
            return "PagingNotExist";
        case 1010 :
            return "EmailAlreadyExist";
        case 1011 :
            return "AlreadyLikeShow";
        case 1012 :
            return "NeedLogin";
        case 1013 :
            return "AlreadyFollowPeople";
        case 1014 :
            return "DidNotFollowPeople";
        case 1015 :
            return "AlreadyFollowBrand";
        case 1016 :
            return "DidNotFollowBrand";
        case 1017 :
            return "PItemNotExist";
        case 1022 :
            return "IsNotAdmin";
    }
};

module.exports = ServerError;
