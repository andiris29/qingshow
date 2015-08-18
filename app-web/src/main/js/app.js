/**
 * Created by wxy325 on 8/19/15.
 */
(function() {
    requirejs.config({
        'baseUrl' : '',
        'paths' : {
            'qs' : './js'
        }
    });

    violet.ui.factory.config({
        'moduleToControllerClass' : function(module) {
            return module;
        },
        'moduleToTemplatePath' : function(module) {
            return module.replace('qs/', './js/') + '.html';
        }
    });

    $(function() {
        require(['qs/core/bootstrap']);
    });
})();