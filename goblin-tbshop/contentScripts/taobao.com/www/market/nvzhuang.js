/**
 * url: http://www.taobao.com/market/nvzhuang/xinpin-zhenzhishan.php?spm=a217f.1226604.a214d5w-static.21.zFYPZO
 *
 * Traversal item with paging, and open shop as child page
 */
var api = new RuntimeAPI({
    'crawl' : function() {
        var body$ = $(document.body);
        body$.scrollTop(body$.height());

        // TODO
        new PageManager();
    }
});

var PageManager = function() {
    this._currentPage = null;
};

var Page = function() {
    // $('.item.next')
    // '.next-disabled'
    // $('.m-itemList .items li')
};
