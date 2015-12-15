(function() {
    // Version 1.0.0
    var goblin = {};

    // ------------------
    // Export
    // ------------------

    // Establish the root object, `window` in the browser, or `exports` on the server.
    var root = this;

    if ( typeof exports !== 'undefined') {
        if ( typeof module !== 'undefined' && module.exports) {
            exports = module.exports = goblin;
        }
        exports.goblin = goblin;
    } else {
        root.goblin = goblin;
    }

    // ------------------
    // Implementation
    // ------------------
    goblin.domain = {
        'TAOBAO' : 'taobao',
        'TMALL' : 'tmall',
        'JAMY' : 'jamy',
        'HM' : 'hm'
    };

    goblin.parseUrl = function(url) {
        var domain = _parseDomain(url),
            id = '';

        if (domain.indexOf('taobao') >= 0) {
            domain = goblin.domain.TAOBAO;
            id = _parseId(url, /id=(\d*)/);
        } else if (domain.indexOf('tmall') >= 0) {
            domain = goblin.domain.TMALL;
            id = _parseId(url, /id=(\d*)/);
        } else if (domain.indexOf('thejamy') >= 0) {
            domain = goblin.domain.JAMY;
            var urls = url.split('/');
            id = urls[urls.length-1];
        } else if (domain.indexOf('hm') >= 0) {
            domain = goblin.domain.HM;
            id = _parseId(url, /page.(\d*)/);
        } else {
            return;
        }

        return {
            'domain': domain,
            'id': id
        }
    };

    var _parseDomain = function(url) {
        var domain;
        //find & remove protocol (http, ftp, etc.) and get domain
        if (url.indexOf("//") > -1) {
            domain = url.split('/')[2];
        } else {
            domain = url.split('/')[0];
        }

        //find & remove port number
        domain = domain.split(':')[0];
        return domain;
    }

    var _parseId = function(source, idRegex) {
        var idComp = source.match(idRegex);
        if (idComp && idComp.length > 1) {
            return idComp[1];
        } else {
            return null;
        }
    };

}.call(this)); 
