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
        var settings = {
            'url' : 'http://chingshow.com/services/system/get?version=' + window.__config.VERSION,
            'type' : 'get',
            'dataType' : 'json',
            'cache' : false,
            'xhrFields' : {
                'withCredentials' : true
            }
        };

        $.ajax(settings).done(function(json){
            window.__config.appServiceRoot = json.data.deployment.appServiceRoot;

            require(['qs/core/bootstrap']);
        });
                
    });
})();