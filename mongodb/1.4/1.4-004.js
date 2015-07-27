// 1.4-004 人气用户可以人为排序
// https://github.com/andiris29/com.focosee.qingshow/issues/318
var cursor = db.peoples.find({
    'roles' : {
        '$elemMatch' : {
            '$eq' : 1
        }
    }
});
while (cursor.hasNext()) {
    var entity = cursor.next();
    if (entity.modelInfo === undefined) {
        entity.modelInfo = {};
    }
    if (entity.modelInfo.order === undefined) {
        entity.modelInfo.order = 0;
        db.peoples.update({
            _id : entity._id
        }, entity);
        print('Set ' + 'people(' + entity._id + ').modelInfo.order to: ' + 0);
    }
}

