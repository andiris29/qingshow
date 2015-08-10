// 2.2-020 清理feeding服务
// https://github.com/andiris29/com.focosee.qingshow/issues/632
if (db.showChosens) {
    db.showChosens.drop();
}

if (db.brands) {
	db.brands.update({}, { "$unset" : { "type" : true}}, false, true);
}