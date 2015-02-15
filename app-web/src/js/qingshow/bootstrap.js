// @formatter:off
define([
    'qingshow/view/ViewFactory'
], function(ViewFactory) {
// @formatter:on
    var fragment = URI(window.location.href).fragment(true);

    var module, options;
    if (fragment.action === 'shareShow') {
        module = '/show/s09/S09ShareShow';
        options = {
            '_id' : fragment._id
        };
    }
    var dom = document.createElement('div');
    $('#root').append(dom);
    ViewFactory.create(module, dom, options, function() {
        // TODO Page transication
    });
});
