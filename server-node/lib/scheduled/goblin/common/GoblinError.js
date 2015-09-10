var util = require('util');

var winston = require('winston');

var GoblinError = function(errorCode, description, err) {
    Error.call(this, 'Goblin Error');
    this.errorCode = errorCode;
    this.description = description || _codeToString(errorCode);
    this.domain = GoblinError.Domain;
    if (errorCode === GoblinError.ServerError) {
        err = err || new Error();
        this.stack = err.stack;
        winston.error(err.errorCode, new Date().toString() + '- GoblinError: ' + this.errorCode);
        winston.error(err.errorCode, '\t' + this.stack);
    }
};

GoblinError.fromCode = function(code) {
    return new GoblinError(code);
};
GoblinError.fromDescription = function(description) {
    return new GoblinError(GoblinError.ServerError, description, new Error());
};
GoblinError.fromError = function(err) {
    return new GoblinError(GoblinError.ServerError, 'Goblin Error: ' + err.toString(), err);
};

util.inherits(GoblinError, Error);

//Domain
GoblinError.Domain = "GoblinError";

//ErrorCode
GoblinError.GoblinError = 2000;
GoblinError.NotSupportItemSource = 2001;
GoblinError.Delist = 2002;
GoblinError.InvalidItemSource = 2003;
GoblinError.NoItemShouldBeCrawl = 2004;

var _codeToString = function (code) {
    switch (code) {
        case GoblinError.GoblinError :
            return "GoblinError";
        case GoblinError.NotSupportItemSource :
            return "NotSupportItemSource";
        case GoblinError.Delist:
            return "Delist";
        case GoblinError.InvalidItemSource:
            return "InvalidItemSource";
        case GoblinError.NoItemShouldBeCrawl:
            return "NoItemShouldBeCrawl";
    }
    return "";
};

module.exports = GoblinError;
