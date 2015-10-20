// 2.2-021 清理预选服务
// https://github.com/andiris29/com.focosee.qingshow/issues/634
if (db.pItems) {
    db.pItems.drop();    
}
if (db.pShow) {
    db.pShow.drop();    
}
