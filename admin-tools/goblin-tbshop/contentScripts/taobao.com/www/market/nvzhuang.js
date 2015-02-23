/**
 * url: http://www.taobao.com/market/nvzhuang/xinpin-zhenzhishan.php?spm=a217f.1226604.a214d5w-static.21.zFYPZO
 *
 * Traversal item with paging, and open shop as child page
 */




var api = new RuntimeAPI({
    'crawl' : function() {
        pageMgr.startCrawl();
    },
    'stopCrawl' : function() {
        pageMgr.stopCrawl();
    }
});

var Page = function(successCallback, errorCallback) {
    this.successCallback = successCallback;
    this.errorCallback = errorCallback;
    this.isStart = false;
};

Page.prototype.startCrawl = function () {
    this.isStart = true;
    this._crawl();
};

Page.prototype.stopCrawl = function () {
    this.isStart = false;
};

Page.prototype._crawl = function() {
    var _this = this;
    if (!this.isStart) {
        return;
    }
    var body$ = $(document.body);
    body$.scrollTop(body$.height());
    async.waterfall([
            function(callback) {
                var retry = 0;
                var load = function() {
                    lis$ = $('.m-itemList .items li .shopname a'); //<a href="shopUrl" target="_blank">showName</a>

                    if (lis$.length === 0) {
                        if (retry < 10) {
                            retry++;
                            _.delay(load, 1000);
                        } else {
                            callback('load fail.');
                        }
                    } else {
                        var shopUrl = [];
                        for (var i = 0; i < lis$.length; i++) {
                            var dom = lis$[i];
                            shopUrl.push($(dom).attr('href'));
                        }

                        //Filter Redundant shopName
                        var filterShopUrls = shopUrl.filter(function (value, index) {
                            return shopUrl.indexOf(value) === index;
                        });
                        api.createSubCrawers(filterShopUrls);
                        callback();
                    }
                };
                load();
            },
            function() {
                var itemNext = $('.item.next');
                if (!itemNext.hasClass('.next-disabled')) {
                    itemNext.click();

                    setTimeout(function (){
                        _this._crawl();
                    }, 1000);
                } else {
                    api.complete();
                    console.log('max page');
                }
            }
        ],
        function(err) {
            api.error(err);
        }
    );
};


var PageManager = function() {
    this._currentPage = new Page();
};

PageManager.prototype.startCrawl = function(){
    this._currentPage.startCrawl();
};
PageManager.prototype.stopCrawl = function(){
    this._currentPage.stopCrawl();
};

var pageMgr = new PageManager();