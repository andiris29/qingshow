// @formatter:off
define([
    'main/services/navigationService',
    'require'
], function(navigationService) {
// @formatter:on
    navigationService.root($('#root').get(0));
    navigationService.push('main/views/login/P01Login');
});
