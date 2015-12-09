var request = require('request'),
    async = require('async'),
    Iconv = require('iconv-lite');
request = request.defaults({'proxy' : 'http://proxy.pvgl.sap.corp:8080'});

var USER_AGENT = 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36';

var _itemIdTmall = '35578088371';

async.series([
   function(callback) {
        // request.get({
            // 'jar' : true,
            // 'url' : 'https://detailskip.taobao.com/json/dyn_combo.do?callback=jsonp594&databiz=promotionPriceMobile&itemId=' + _itemIdTmall,
            // 'headers' : {
                // 'user-agent': USER_AGENT,
                // 'referer' : 'http://detail.tmall.com/item.htm?id=' + _itemIdTmall,
                // 'accept-language' : 'en,en-US;q=0.8,zh-CN;q=0.6,zh;q=0.4'
            // },
            // 'encoding' : 'binary',
        // }, function(err, response, body) {
            // callback();
        // });
        callback();
   } 
], function(err) {
    request.get({
        // 'jar' : true,
        'url' : 'http://mdskip.taobao.com/core/initItemDetail.htm?callback=setMdskip&itemId=' + _itemIdTmall,
        'headers' : {
            'user-agent' : USER_AGENT,
            'referer' : 'http://detail.tmall.com/item.htm?id=' + _itemIdTmall,
            'accept-language' : 'en,en-US;q=0.8,zh-CN;q=0.6,zh;q=0.4',
            'cookie' : 'lgc=multiinterface; tracknick=multiinterface;cookie2=1cabb08f7d582ed0345e8c466ba2f402;'
        },
        'encoding' : 'binary',
        
    }, function(err, response, body) {
        var b = Iconv.decode(new Buffer(body, 'binary'), 'gbk');
        console.log('--- Tmall ---');
        console.log(b);
    });
});

/*
GET /core/initItemDetail.htm?callback=setMdskip&itemId=521244578003 HTTP/1.1
Host: mdskip.taobao.com
referer: http://detail.tmall.com/item.htm?id=521244578003
accept-language: en,en-US;q=0.8,zh-CN;q=0.6,zh;q=0.4
Cache-Control: no-cache
Postman-Token: 631ae496-57b7-f985-ad47-2327f46e24ef
*/
