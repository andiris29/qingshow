(function() {
    // Environment by url hash & search
    var uri = URI(location.href);
    var env = {
        'uri' : uri,
        'uriQuery' : uri.query(true),
        'uriFragment' : uri.fragment(true)
    };
    env.nocache = env.uriQuery.nocache === 'true' || env.uriFragment.nocache === 'true';
    env.fake = env.uriQuery.fake === 'true' || env.uriFragment.fake === 'true';
    // OO
    var oo = {};
    oo.extend = function(subClz, superClz) {
        var subClzPrototype = subClz.prototype;
        // add the superclass prototype to the subclass definition
        subClz.superclass = superClz.prototype;
        // copy prototype
        var F = function() {
        };
        F.prototype = superClz.prototype;
        subClz.prototype = new F();
        for (var prop in subClzPrototype) {
            if (subClzPrototype.hasOwnProperty(prop)) {
                subClz.prototype[prop] = subClzPrototype[prop];
            }
        }
        subClz.prototype.constructor = subClz;
        if (superClz.prototype.constructor == Object.prototype.constructor) {
            superClz.prototype.constructor = superClz;
        }
        return subClz;
    };
    // String utils
    var string = {};
    string.substitute = function(str, rest) {
        if (!str) {
            return '';
        }
        for (var i = 1; i < arguments.length; i++) {
            str = str.replace(new RegExp("\\{" + (i - 1) + "\\}", "g"), arguments[i]);
        }
        return str;
    };
    // Export
    var andrea = window.andrea = {
        'env' : env,
        'oo' : oo,
        'string' : string
    };
})();
