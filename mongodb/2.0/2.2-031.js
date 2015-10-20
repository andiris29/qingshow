// 2.2-031 移除时尚情报
// https://github.com/andiris29/com.focosee.qingshow/issues/661
if (db.previews) {
    db.previews.drop();    
}

if (db.rPeopleLikePreview) {
	db.rPeopleLikePreview.drop();
}