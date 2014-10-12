var mongoose = require('mongoose');


function limitQuery(query, pageNo, pageSize){
    pageNo = pageNo || 1;
    pageSize = pageSize || 10;
    return query.skip((pageNo - 1) * pageSize)
        .limit(pageSize);
}
function stringArrayToObjectIdArray(stringArray){
    var retArray = [];
    stringArray.forEach(function(idStr) {
        var objId = mongoose.mongo.BSONPure.ObjectID(idStr);
        retArray.push(objId);
    });
    return retArray;
}
module.exports = {
    'limitQuery' : limitQuery,
    'stringArrayToObjectIdArray' : stringArrayToObjectIdArray

};
