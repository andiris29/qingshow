// @formatter:off
define([
    'main/services/TemplateService'
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
            var view;
            if (!err) {
                var Module = results[0];
                var template$ = results[1];

                view = new Module($(dom).append(template$).get(0), options);
            }
            if (callback) {
                callback(err, view);
            }
        });
    };

    return ViewFactory;
});
