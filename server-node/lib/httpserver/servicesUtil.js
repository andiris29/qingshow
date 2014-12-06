var mongoose = require('mongoose');
var ServerError = require('./server-error');

function responseError(res, err) {
    if (!err) {
        err = new ServerError(1000, 'unknown error');
    } else if (!( err instanceof ServerError)) {
        err = new ServerError(1000, err);
    } else if (!err.errorCode) {
        err = new ServerError(1000, err.description);
    }

    res.json({
        //metadata.error
        metadata : {
            error : err.errorCode,
            devInfo : err
        }
    });
}

function stringArrayToObjectIdArray(stringArray) {
    var retArray = [];
    stringArray.forEach(function(idStr) {
        try {
            var objId = mongoose.mongo.BSONPure.ObjectID(idStr);
            retArray.push(objId);
        } catch(e) {
        }
    });
    return retArray;
}

module.exports = {
    'stringArrayToObjectIdArray' : stringArrayToObjectIdArray,
    'responseError' : responseError
};
