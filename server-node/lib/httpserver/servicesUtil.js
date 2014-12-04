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

function limitQuery(query, pageNo, pageSize) {
    pageNo = pageNo || 1;
    pageSize = pageSize || 10;
    return query.skip((pageNo - 1) * pageSize).limit(pageSize);
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

function sendSingleQueryToResponse(res, queryGenFunc, additionFunc, dataGenFunc, pageNo, pageSize, modelFinalHandler, finishCallback) {
    var query;
    query = queryGenFunc();
    query.count(function(err, count) {
        var numPages;
        if (err) {
            responseError(res, err);
            return;
        }
        if ((pageNo - 1) * pageSize > count) {
            responseError(res, new ServerError(ServerError.PagingNotExist));
            return;
        }
        numPages = parseInt((count + pageSize - 1) / pageSize);
        query = queryGenFunc();
        limitQuery(query, pageNo, pageSize);
        if (additionFunc) {
            additionFunc(query);
        }
        query.exec(function(err, shows) {
            if (!err) {
                if (modelFinalHandler) {
                    modelFinalHandler(shows, function(s) {
                        var retData = {
                            metadata : {
                                //TODO change invilidateTime
                                "numTotal" : count,
                                "numPages" : numPages,
                                //                        "refreshTime": 3600000
                            },
                            data : dataGenFunc(s)
                        };
                        res.json(retData);
                        if (finishCallback) {
                            finishCallback();
                        }
                    });
                } else {
                    var retData = {
                        metadata : {
                            //TODO change invilidateTime
                            "numTotal" : count,
                            "numPages" : numPages,
                            //                        "refreshTime": 3600000
                        },
                        data : dataGenFunc(shows)
                    };
                    res.json(retData);
                    if (finishCallback) {
                        finishCallback();
                    }
                }
            } else {
                responseError(res, err);
                if (finishCallback) {
                    finishCallback();
                }
            }
        });
    });
}

module.exports = {
    'limitQuery' : limitQuery,
    'stringArrayToObjectIdArray' : stringArrayToObjectIdArray,
    'sendSingleQueryToResponse' : sendSingleQueryToResponse,
    'responseError' : responseError
};
