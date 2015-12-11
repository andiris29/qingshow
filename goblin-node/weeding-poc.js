var request = require('request'),
    request = request.defaults({'proxy' : 'http://proxy.pvgl.sap.corp:8080'}),
    async = require('async'),
    Iconv = require('iconv-lite');

var _userAgent = 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36',
    _tbItemId = '520512841799';

async.series([
    function(callback) {
        request.get({
            'jar' : true,
            'url' : 'https://detailskip.taobao.com/json/dyn_combo.do?callback=jsonp594&databiz=promotionPriceMobile&itemId=' + _tbItemId,
            'headers' : {
                'user-agent': _userAgent,
                'referer' : 'http://detail.tmall.com/item.htm?id=' + _tbItemId,
                'accept-language' : 'en,en-US;q=0.8,zh-CN;q=0.6,zh;q=0.4'
            },
            'encoding' : 'binary',
        }, function(err, response, body) {
            callback();
        });
        
    }
], function(err, result) {
    request.get({
        'jar' : true,
        'url' : 'https://rate.taobao.com/feedRateList.htm?auctionNumId=' + _tbItemId + '&currentPageNum=1&pageSize=20&rateType=-1&orderType=sort_weight',
        'headers' : {
            'user-agent' : _userAgent,
            'referer' : 'http://detail.tmall.com/item.htm?id=' + _tbItemId,
            'accept-language' : 'en,en-US;q=0.8,zh-CN;q=0.6,zh;q=0.4'
        },
        'encoding' : 'binary',
    }, function(err, response, body) {
        var b = Iconv.decode(new Buffer(body, 'binary'), 'gbk');
        console.log(b);
    });
});
