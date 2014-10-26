var util = require('util');

var ServerError = function (errorCode) {
    Error.call(this, 'server error');
    this.errorCode = errorCode;
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

module.exports = ServerError;