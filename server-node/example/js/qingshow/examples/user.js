// @formatter:off
define([
    'qingshow/services/HTTPService'
], function(HTTPService) {
// @formatter:on
    return {
        'login' : function() {
            HTTPService.post('/user/login', {
                'idOrNickName' : 'wangzheng',
                'password' : 'wangzheng'
            });
        },
        'saveReceiver' : function() {
            HTTPService.post('/user/saveReceiver', {
                'name' : '王铮',
                'phone' : '13524183713',
                'province' : '上海／上海／浦东',
                'address' : '晨晖路'
            });
        }
    };
});
