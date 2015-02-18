define([], function Setup() {
    var devicePixRatio = window.devicePixelRatio || 1;
    var versionSearchString;
    var searchString = function(data) {
        for (var i = 0; i < data.length; i++) {
            var dataString = data[i].string;
            var dataProp = data[i].prop;
            versionSearchString = data[i].versionSearch || data[i].identity;
            if (dataString) {
                if (dataString.indexOf(data[i].subString) != -1) {
                    return data[i].identity;
                }
            } else if (dataProp) {
                return data[i].identity;
            }
        }
    };
    var seps = [';', ' '];
    var searchVersion = function(dataString) {
        var index = dataString.indexOf(versionSearchString);
        if (index == -1) {
            return;
        }
        var versionStr = dataString.substring(index + versionSearchString.length + 1);
        var i = 0, l = seps.length, sepIndex = -1;
        while (sepIndex === -1 && i < l) {
            sepIndex = versionStr.indexOf(seps[i++]);
        }
        if (sepIndex !== -1) {
            versionStr = versionStr.slice(0, sepIndex);
        }
        return versionStr;
    };
    var dataBrowser = [{
        string : navigator.userAgent,
        subString : "Chrome",
        identity : "Chrome"
    }, {
        string : navigator.userAgent,
        subString : "OmniWeb",
        versionSearch : "OmniWeb/",
        identity : "OmniWeb"
    }, {
        string : navigator.userAgent,
        subString : "Safari",
        identity : "Safari",
        versionSearch : "Version"
    }, {
        string : navigator.userAgent,
        subString : "Apple",
        identity : "UIWebView",
        versionSearch : "AppleWebKit"
    }, {
        prop : window.opera,
        identity : "Opera",
        versionSearch : "Version"
    }, {
        string : navigator.vendor,
        subString : "iCab",
        identity : "iCab"
    }, {
        string : navigator.vendor,
        subString : "KDE",
        identity : "Konqueror"
    }, {
        string : navigator.userAgent,
        subString : "Firefox",
        identity : "Firefox"
    }, {
        string : navigator.vendor,
        subString : "Camino",
        identity : "Camino"
    }, {// for newer Netscapes (6+)
        string : navigator.userAgent,
        subString : "Netscape",
        identity : "Netscape"
    }, {
        string : navigator.userAgent,
        subString : "MSIE",
        identity : "Explorer",
        versionSearch : "MSIE"
    }, {
        string : navigator.userAgent,
        subString : ".NET",
        identity : "Explorer"
    }, {
        string : navigator.userAgent,
        subString : "Gecko",
        identity : "Mozilla",
        versionSearch : "rv"
    }, {// for older Netscapes (4-)
        string : navigator.userAgent,
        subString : "Mozilla",
        identity : "Netscape",
        versionSearch : "Mozilla"
    }];

    var dataOS = [{
        string : navigator.userAgent,
        subString : "Windows Phone",
        identity : "WindowsPhone"
    }, {
        string : navigator.platform,
        subString : "Win",
        identity : "Windows"
    }, {
        string : navigator.platform,
        subString : "Mac",
        identity : "Mac"
    }, {
        string : navigator.userAgent,
        subString : "iPhone",
        identity : "iPhone/iPod"
    }, {
        string : navigator.userAgent,
        subString : "iPad",
        identity : "iPad"
    }, {
        string : navigator.platform,
        subString : "Linux",
        identity : "Linux"
    }];

    var dataRenderEngine = [{
        string : navigator.userAgent,
        subString : "Presto",
        identity : "Presto"
    }, {
        string : navigator.userAgent,
        subString : "KHTML",
        identity : "KHTML"
    }, {
        string : navigator.userAgent,
        subString : "Gecko",
        identity : "Gecko"
    }, {
        string : navigator.userAgent,
        subString : "Trident",
        identity : "Trident"
    }];

    var dataMobile = [{
        string : navigator.userAgent,
        subString : 'Touch',
        identity : 'Touch'
    }, {
        string : navigator.userAgent,
        subString : 'Android',
        identity : 'Android',
    }, {
        string : navigator.userAgent,
        subString : 'iPhone',
        identity : 'iPhone'
    }, {
        string : navigator.userAgent,
        subString : 'iPad',
        identity : 'iPad'
    }];

    var _userAgent = searchString(dataBrowser) || "An unknown browser", _version = searchVersion(navigator.userAgent) || searchVersion(navigator.appVersion) || "an unknown version", _isIE = searchString(dataBrowser) === "Explorer", _isFirefox = searchString(dataBrowser) === 'Firefox', _isChrome = searchString(dataBrowser) === 'Chrome', _isSafari = searchString(dataBrowser) === 'Safari', _isUIWebView = searchString(dataBrowser) === 'UIWebView', _os = searchString(dataOS) || "an unknown OS", _renderEngine = searchString(dataRenderEngine) || "An unknown RenderEngine", _mobile = searchString(dataMobile), _pointerEnabled = navigator.pointerEnabled === true;
    var UADetector = {
        userAgent : function() {
            return _userAgent;
        },
        version : function() {
            return _version;
        },
        isIE : function() {
            return _isIE;
        },
        isFirefox : function() {
            return _isFirefox;
        },
        isChrome : function() {
            return _isChrome;
        },
        isSafari : function() {
            return _isSafari;
        },
        isUIWebView : function() {
            return _isUIWebView;
        },
        os : function() {
            return _os;
        },
        devicePixelRatio : function() {
            return devicePixRatio;
        },
        renderEngine : function() {
            return _renderEngine;
        },
        isMobile : function() {
            return _mobile !== undefined;
        },
        isIOS : function() {
            return _mobile === "iPhone" || _mobile === "iPad";
        },
        isWindowsSurface : function() {
            return ((_os === 'Windows' || _os === 'WindowsPhone') && (_mobile === 'Touch'));
        },
        isWindowsPhone : function() {
            return ((_os === 'WindowsPhone') && (_mobile === 'Touch'));
        },
        isAndroid : function() {
            return _mobile === "Android";
        },
        isPointerEnabled : function() {
            return _isIE && _pointerEnabled;
        }
    };
    return (UADetector);
}); 