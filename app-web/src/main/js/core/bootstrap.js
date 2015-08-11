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
        // 'root' : 'http://127.0.0.1:30001/services',
        'root' : 'http://121.41.161.239/services',
        'version' : '2.1.0'
    });
    navigationService.config({
        'root' : $('#root').get(0)
    });
    window.__services = {
        'httpService' : httpService,
        'navigationService' : navigationService,
        'downloadService' : downloadService
    };

    // Bootstrap first page
    var search = violet.url.search;
    if (search.entry === 'shareShow') {
        navigationService.push('qs/views/P02SharingShow', {
            '_id' : search._id
        });
    } else if (search.entry === 'shareTrade') {
        navigationService.push('qs/views/P03SharingTrade', {
            '_id' : search._id
        });
    } else {
        navigationService.push('qs/views/P01NotFound');
    }
});
