/**
 * url:
 * http://aoyp.taobao.com/shop/view_shop.htm?spm=a217f.1226604.1998111760.148.zFYPZO&user_number_id=94781032&ssid=r11
 *
 * Redirect to http://shop$shopId.taobao.com/search.htm?search=y&orderType=hotsell_desc
 */
// redirect to search page
// eg: http://shop35593924.taobao.com/search.htm?search=y&orderType=hotsell_desc

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
    if (!this.isStart) {
        return;
    }


    async.waterfall([
            function (callback) {
                var scoreTitles = $('.dsr-title');
                var scoreNums = $('.dsr-num');
                var scoreEntity = {};
                var keyMap = {
                    "描述" : "item_score",
                    "物流" : "delivery_score",
                    "服务" : "service_score"
                };
                for (var i = 0; i < scoreTitles.length; i++) {
                    var t = scoreTitles[i].innerText;
                    var s = parseFloat(scoreNums[i].innerText);
                    var scoreKey = keyMap[t];
                    scoreEntity[scoreKey] = s;
                }
                callback(null, scoreEntity);
            },
            function (scoreEntity, callback) {
                //TODO http request

                callback();
            }, function (callback) {
                //TODO createSubCrawers
                var locationOrigin = window.location.origin;
                var searchLocation = locationOrigin + '/search.htm?search=y&orderType=hotsell_desc';
                api.createSubCrawers([searchLocation]);

            }
        ],
        function (err) {
            if (err) {
                api.error(err);
            } else {
                setTimeout(function(){
                    api.complete();
                }, 0);
            }
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