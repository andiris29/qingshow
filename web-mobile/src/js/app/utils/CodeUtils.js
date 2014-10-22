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
            'women' : '潮流女装',
            'bag' : '时尚包包',
            'shoe' : '个性潮鞋',
            'accessories' : '百搭配饰'
        },
        'people.role' : {
            'user' : '用户',
            'model' : '设计师' // TODO 模特？
        },
        'people.gender' : {
            'male' : '男性',
            'female' : '女性'
        },
        'people.hairType' : {
            'all' : '所有',
            'long' : '长发',
            'superlong' : '超长发',
            'midlong' : '中长发'
        },
        'item.category' : {
            'wear' : '上装',
            'pants' : '裤子',
            'skirts' : '裙子',
            'shoe' : '鞋子'
        }
    };

    CodeUtils.getValue = function(key, code) {
        return CodeUtils.codeTable[key][code];
    };

    return CodeUtils;
});
