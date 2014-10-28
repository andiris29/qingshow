// @formatter:off
define([
], function() {
// @formatter:on

    /**
     *
     *
     */
    var CodeUtils = {};

    CodeUtils.codeTable = {
        'show.tag' : {
            0 : '潮流女装',
            1 : '时尚包包',
            2 : '个性潮鞋',
            3 : '百搭配饰'
        },
        'people.role' : {
            0 : '用户',
            1 : '设计师' // TODO 模特？
        },
        'people.gender' : {
            0 : '男性',
            1 : '女性'
        },
        'people.hairType' : {
            0 : '所有',
            1 : '长发',
            2 : '超长发',
            3 : '中长发'
        },
        'item.category' : {
            0 : '上装',
            1 : '裤子',
            2 : '裙',
            3 : '鞋子'
        },
        'server.error': {
            1000: '服务器出错',
            1001: '账号或密码错误',
            1002: '长时间未登录，请重新登录',
            1003: 'Show Not Exist',
            1004: 'Item Not Exist',
            1005: 'People Not Exist',
            1006: 'Brand Not Exist',
            1007: '无效的邮箱',
            1008: '参数个数不足',
            1009: 'Paging Not Exist',
            1010: '输入邮箱已被使用，请重新输入'
        }
    };

    CodeUtils.getValue = function(key, code) {
        return CodeUtils.codeTable[key][code];
    };

    return CodeUtils;
});
