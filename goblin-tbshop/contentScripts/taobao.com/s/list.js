/**
 * url: http://s.taobao.com/list?spm=a217f.1226604.a214d5w-static.5.Wr0btB&q=%B4%BF%C9%AB&cat=16&style=grid&seller_type=taobao&cps=yes&s=0&cat=50102538
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
}

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
                    lis$ = $('.st-item .seller a'); //<a href="shopUrl" target="_blank">showName</a>

                    if (lis$.length === 0) {
                        if (retry < 10) {
                            retry++;
                            _.delay(load, 1000);
                        } else {
                            callback('load fail.');
                        }
                    } else {
                        var shopNames = [];
                        for (var i = 0; i < lis$.length; i++) {
                            var dom = lis$[i];
                            shopNames.push(dom.innerHTML);
                        }

                        //Filter Redundant shopName
                        var filterShopNames = shopNames.filter(function (value, index) {
                            return shopNames.indexOf(value) === index;
                        });

                        //TODO http request
                        $.getJSON('http://121.41.162.102:30001/services/feeding/chosen', {}, function (data){
                            var topShops = data.topShops;
                            //shopId;
                            //http://shop{shop_id}.taobao.com/search.htm?search=y&orderType=hotsell_desc
                            //http://shop107823567.taobao.com/search.htm?search=y&orderType=hotsell_desc
                            var shopIds = ["107823567"];
                            var urls = shopIds.map(function (sId) {
                                return "http://shop" + sId + ".taobao.com/search.htm?search=y&orderType=hotsell_desc";
                            });
                            api.createSubCrawers(urls);
                            callback(null, lis$);
                        });

                    }
                };
                load();
            },
            function(lis$) {
                var itemNext = $('.page-next');
                var disableBtn = $(".icon-btn-next-2-disable", itemNext);
                if (!disableBtn.length) {
                    itemNext[0].click();

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