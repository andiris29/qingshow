/**
 * url: http://www.taobao.com/market/nvzhuang/xinpin-zhenzhishan.php?spm=a217f.1226604.a214d5w-static.21.zFYPZO
 *
 * Traversal item with paging, and open shop as child page
 */
var api = new RuntimeAPI({
    'crawl' : function() {
        var body$ = $(document.body);
        body$.scrollTop(body$.height());

        new PageManager();
    }
});

var PageManager = function() {
    this._currentPage = new Page();
};

var Page = function(successCallback, errorCallback) {
    async.waterfall([
    function(callback) {
        var retry = 0;
        var load = function() {
            lis$ = $('.m-itemList .items li');
            if (lis$.length === 0) {
                if (retry < 10) {
                    retry++;
                    _.delay(load, 1000);
                } else {
                    callback('load fail.');
                }
            } else {
                callback(null, lis$);
            }
        };
    },
    function(lis$) {
        // $('.item.next')
        // '.next-disabled'
    }], function(err) {

    });
}; 