/**
 * url: http://dsup.taobao.com/search.htm?search=y&orderType=hotsell_desc
 * 
 * Inspect the top sales and save to db
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
                    lis$ = $('.shop-hesper-bd .item');

                    if (lis$.length === 0) {
                        if (retry < 10) {
                            retry++;
                            _.delay(load, 1000);
                        } else {
                            callback('load fail.');
                        }
                    } else {

                        var nick = $(".shop-name a")[0].innerText;

                        var hotSales = [];
                        for (var i = 0; i < lis$.length; i++) {
                            var dom = lis$[i];

                            var num_iid = dom.getAttribute('data-id');
                            var detailHerfDom = $('.detail a', dom)[0];
                            var title = detailHerfDom.innerHTML;
                            var price = $(".c-price", dom)[0].innerHTML;
                            var numSold = $(".sale-num", dom)[0].innerHTML;

                            var sale = {
                                num_iid : num_iid,
                                title: title,
                                __context : {
                                    price : price,
                                    numSold : numSold
                                }
                            };
                            hotSales.push(sale);
                        }

                        var shopEntity = {
                            nick : nick,
                            __context : {
                                hotSales : hotSales
                            }
                        };
                        callback(null, shopEntity);

                    }
                };
                load();
            },
            function(shopEntity) {
                console.log(shopEntity);
                //TODO http request
                api.complete();


            }
        ],
        function(err) {
            api.error(err);
            /*
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
             */
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