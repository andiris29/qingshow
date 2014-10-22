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
            2 : '裙子',
            3 : '鞋子'
        }
    };

    CodeUtils.getValue = function(key, code) {
        return CodeUtils.codeTable[key][code];
    };

    return CodeUtils;
});
