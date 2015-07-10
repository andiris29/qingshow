// @formatter:off
define([
    'main/core/viewManeger',
    'require'
], function(viewManeger) {
// @formatter:on

    viewManeger.root($('#root').get(0));
    viewManeger.push('main/views/login/P01Login');
});
