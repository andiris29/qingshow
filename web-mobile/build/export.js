//Export modules in the list to global scope
(function(global) {
    // @formatter:off
    var list = [
    ];
    // @formatter:on
    var jq = global.jQuery;

    if (jq) {
        if (jQuery.holdReady) {
            jQuery.holdReady(true);
        } else {
            jQuery.readyWait += 1;
        }
    }
    //put modules on global object
    require(list, function() {
        var exports = arguments;

        list.forEach(function(mod, i) {
            var nameParts = mod.split("/");
            nameParts.reduce(function(p, c, index) {
                if (!p[c]) {
                    if (index < nameParts.length - 1) {
                        p[c] = {};
                    } else {
                        p[c] = exports[i];
                    }
                }
                return p[c];
            }, global);
        });
        if (jq) {
            if (jQuery.holdReady) {
                jQuery.holdReady(false);
            } else {
                jQuery.ready(true);
            }
        }
    });

})(this);
