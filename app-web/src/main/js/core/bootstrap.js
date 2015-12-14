// @formatter:off
define([
    'qs/services/httpService',
    'qs/services/navigationService',
    'qs/services/downloadService',
    'qs/core/model',
    'require'
], function(httpService, 
    navigationService, 
    downloadService, 
    model) {
// @formatter:on

    // Initialize __runtime
    window.__runtime = {
        'model' : model
    };
    // Initialize __services
    httpService.config({
        'root' : window.__config.appServiceRoot,
        'version' : window.__config.VERSION
    });
    navigationService.config({
        'root' : $('#root').get(0)
    });
    window.__services = {
        'httpService' : httpService,
        'navigationService' : navigationService,
        'downloadService' : downloadService
    };



    // httpService.request('/user/loginAsViewer', 'post', {
        
    // }, function(err, metadata, data) {
    //     if(err)
    //     {
    //         //print error
    //     }
    //     else
    //     {
    //       //  print data

    //     }
    // });

    // Bootstrap first page
    var search = violet.url.search;
 httpService.request('/trace/openShare', 'post', {
        '_id' : search._id
    }, function(err, metadata, data) {});


    httpService.request('/share/query', 'get', {
        '_ids' : [search._id]
    }, function(err, metadata, data) {
        var shareObj = data && data.sharedObjects && data.sharedObjects[0];
        if (err || !shareObj) {
            navigationService.push('qs/views/P01NotFound');
        } else {

           
            if (shareObj.type === 0) {
                //show
                navigationService.push('qs/views/P02ShareShow', {
                    'entity' : shareObj.targetInfo
                });
            } else if (shareObj.type === 1) {
                //trade
                navigationService.push('qs/views/P03ShareTrade', {
                    'entity' : shareObj.targetInfo
                });
            } else if (shareObj.type === 2) {
                //bonus
                navigationService.push('qs/views/P04ShareBonus', {
                    'create' : shareObj.create,
                    '_id' : search._id
                });
            } else {
                navigationService.push('qs/views/P01NotFound');
            }
        }
    });
});
