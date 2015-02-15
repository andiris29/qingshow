// @formatter:off
define([
    'qingshow/services/TemplateService'
], function(TemplateService) {
// @formatter:on
    var ViewFactory = {};

    ViewFactory.create = function(module, dom, options, callback) {
        async.parallel([
        function(callback) {
            require(['qingshow/view' + module], function(Module) {
                callback(null, Module);
            });
        },
        function(callback) {
            TemplateService.load('./templates' + module + '.html', callback);
        }], function(err, results) {
            if (err) {
                callback(err);
            } else {
                var Module = results[0];
                var template$ = results[1];

                callback(null, new Module($(dom).append(template$).get(0), options));
            }
        });
    };

    return ViewFactory;
});
