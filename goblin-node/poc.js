var request = require('request');
var Iconv = require('iconv-lite');


var tbItemId = '521243864338';
request.get({
    'url' : 'http://mdskip.taobao.com/core/initItemDetail.htm?callback=setMdskip&itemId=' + tbItemId,
    'headers' : {
        'user-agent':'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36',
        'referer' : 'http://detail.tmall.com/item.htm?id=' + tbItemId,
        'accept-language' : 'en,en-US;q=0.8,zh-CN;q=0.6,zh;q=0.4'
    },
    'encoding' : 'binary',
    
}, function(err, response, body) {
    var b = Iconv.decode(new Buffer(body, 'binary'), 'gbk');
    console.log(b);
}); 

/*
GET /core/initItemDetail.htm?callback=setMdskip&itemId=521244578003 HTTP/1.1
Host: mdskip.taobao.com
referer: http://detail.tmall.com/item.htm?id=521244578003
accept-language: en,en-US;q=0.8,zh-CN;q=0.6,zh;q=0.4
Cache-Control: no-cache
Postman-Token: 631ae496-57b7-f985-ad47-2327f46e24ef
*/
