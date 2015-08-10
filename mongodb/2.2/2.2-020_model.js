//2.2-020 清理模特服务
// https://github.com/andiris29/com.focosee.qingshow/issues/631
if (db.rPeopleFollowPeople) {
    db.rPeopleFollowPeople.drop();
}

if (db.peoples) {
	db.peoples.update({}, { "$unset" : { "roles" : true}}, false, true);
	db.peoples.update({}, { "$unset" : { "name" : true}}, false, true);
	db.peoples.update({}, { "$unset" : { "birthday" : true}}, false, true);
	db.peoples.update({}, { "$unset" : { "job" : true}}, false, true);
	db.peoples.update({}, { "$unset" : { "modelInfo" : true}}, false, true);
}