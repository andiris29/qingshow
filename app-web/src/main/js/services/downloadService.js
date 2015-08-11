// @formatter:off
define([
], function() {
// @formatter:on
    var downloadService = {};

    downloadService.download = function() {
        if (window.WeixinJSBridge || navigator.userAgent.indexOf('Android') !== -1) {
            window.open('http://a.app.qq.com/o/simple.jsp?pkgname=com.focosee.qingshow');
        } else {
            window.open('https://itunes.apple.com/us/app/qing-xiu/id946116105?ls=1&mt=8');
        }
    };

    return downloadService;
});
