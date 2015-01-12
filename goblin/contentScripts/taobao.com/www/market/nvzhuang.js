// http://www.taobao.com/market/nvzhuang/maoyi.php
var api = new RuntimeAPI({
    'crawl' : function() {
        var body$ = $(document.body);
        body$.scrollTop(body$.height());

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
