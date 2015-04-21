// Copy video from show to items.
db.getCollection('shows').find({}).forEach(function(show) {
    if (!show.itemRefs) {
        return;
    }
    show.itemRefs.forEach(function(_id) {
        var item = db.getCollection('items').findOne({
            '_id' : _id
        });
        if (item) {
            item.video = show.video;
            print(item.video);
        }
    });
});
