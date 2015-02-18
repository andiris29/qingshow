// @formatter:off
define([
    'qingshow/view/ViewFactory'
], function(ViewFactory) {
// @formatter:on
    var query = URI(window.location.href).query(true);

    var module, options;
    if (query.action === 'shareShow') {
        module = '/show/s09/S09ShareShow';
        options = {
            '_id' : query._id
        };
    }
    var dom = document.createElement('div');
    $('#root').append(dom);
    ViewFactory.create(module, dom, options, function() {
        // TODO Page transication
    });
});
