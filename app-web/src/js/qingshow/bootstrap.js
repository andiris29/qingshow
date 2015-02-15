define([], function() {
    var fragment = URI(window.location.href).fragment(true);

    var module, options;
    if (fragment.action === 'shareShow') {
        module = 'qingshow/view/show/s09/S09ShareShow';
        options = {
            '_id' : fragment._id
        };
    }
    require([module], function(Module) {
        new Module($('.qs-s09').get(0), options);
    });
});
