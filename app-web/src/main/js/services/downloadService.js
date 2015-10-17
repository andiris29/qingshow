// @formatter:off
define([
], function() {
// @formatter:on
    var downloadService = {};

    downloadService.download = function() {

        __services.httpService.request('/trace/downloadViaShare', 'post', {
            '_id' : violet.url.search._id || ""
        }, function(err, metadata, data) {});

        if (window.WeixinJSBridge || navigator.userAgent.indexOf('Android') !== -1) {
            window.open('http://a.app.qq.com/o/simple.jsp?pkgname=com.focosee.qingshow');
        } else {
            window.open('https://itunes.apple.com/us/app/qing-xiu/id946116105?ls=1&mt=8');
        }
    };

    return downloadService;
});
