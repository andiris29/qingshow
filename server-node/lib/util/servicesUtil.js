var mongoose = require('mongoose');

function responseError(res, err) {
    if (!err.code) {
        err.code = 1000;
    }
    res.json({
        //metadata.error
        metadata: {
            error: err.code,
            devInfo: err
        }
    });
}

function limitQuery(query, pageNo, pageSize) {
    pageNo = pageNo || 1;
    pageSize = pageSize || 10;
    return query.skip((pageNo - 1) * pageSize)
        .limit(pageSize);
}
function stringArrayToObjectIdArray(stringArray) {
    var retArray = [];
    stringArray.forEach(function (idStr) {
        var objId = mongoose.mongo.BSONPure.ObjectID(idStr);
        retArray.push(objId);
    });
    return retArray;
}

function sendSingleQueryToResponse(res, queryGenFunc, additionFunc, dataGenFunc, pageNo, pageSize) {
    var query;
    query = queryGenFunc();
    query.count(function (err, count) {
        var numPages;
        if (err) {
            responseError(res, err);
            return;
        }
        numPages = parseInt((count + pageSize - 1) / pageSize);
        query = queryGenFunc();
        limitQuery(query, pageNo, pageSize);
        additionFunc(query);
        query.exec(function (err, shows) {
            if (!err) {
                var retData = {
                    metadata: {
                        //TODO change invilidateTime
                        "numPages": numPages,
                        "invalidateTime": 3600000
                    },
                    data: dataGenFunc(shows)
                };
                res.json(retData);
            } else {
                responseError(res, err);
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
