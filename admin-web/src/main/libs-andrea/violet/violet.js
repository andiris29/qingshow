// OOUtil
var _extend = function(subClz, superClz) {
    var subClzPrototype = subClz.prototype;
    // Add the superclass prototype to the subclass definition
    subClz.superclass = superClz.prototype;
    // Copy prototype
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
// StringUtil
var _substitute = function(str, rest) {
    if (!str) {
        return '';
    }
    for (var i = 1; i < arguments.length; i++) {
        str = str.replace(new RegExp("\\{" + (i - 1) + "\\}", "g"), arguments[i]);
    }
    return str;
};

window.violet = {
    'oo' : {
        'extend' : _extend
    },
    'string' : {
        'substitute' : _substitute
    }
}; 