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
        }
    };

    CodeUtils.getValue = function(key, code) {
        return CodeUtils.codeTable[key][code];
    };

    return CodeUtils;
});
