// @formatter:off
define([
], function() {
// @formatter:on
    window.qs = window.qs || {};
    window.qs.examples = window.qs.examples || {};

    var _expose = function(moduleName) {
        window.qs.examples[moduleName] = window.qs.examples[moduleName] || {};
        require(['qingshow/examples/' + moduleName], function(module) {
            for (var key in module) {
                window.qs.examples[moduleName][key] = module[key];
            }
        });
    };
    _expose('trade');
    _expose('user');
});
