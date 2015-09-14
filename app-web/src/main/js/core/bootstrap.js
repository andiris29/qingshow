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
        'root' : 'http://chingshow.com/services',
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
    var entry = search.entry || search.action;
    if (entry === 'shareShow') {
        navigationService.push('qs/views/P02ShareShow', {
            '_id' : search._id
        });
    } else if (entry === 'shareTrade') {
        navigationService.push('qs/views/P03ShareTrade', {
            '_id' : search._id
        });
    } else if (entry === 'shareBonus') {
        navigationService.push('qs/views/P04ShareBonus', {
        });
    } else {
        navigationService.push('qs/views/P01NotFound');
    }

});
