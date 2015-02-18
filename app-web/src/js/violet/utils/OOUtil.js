define([], function() {
    var OOUtil = {};

    OOUtil.extend = function(subClz, superClz) {
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

    return OOUtil;
});
