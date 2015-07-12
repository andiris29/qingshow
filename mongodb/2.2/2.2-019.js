// 2.2-019 清理品牌服务
// https://github.com/andiris29/com.focosee.qingshow/issues/630
if (db.rPeopleFollowBrand) {
    db.rPeopleFollowBrand.drop();
}

if (db.items) {
	db.items.update({}, { "$unset" : { "brandNewInfo" : true}}, false, true);
	db.items.update({}, { "$unset" : { "brandDiscountInfo" : true}}, false, true);
}