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

    CodeUtils.getCodes = function(key) {
        return CodeUtils.codeTable[key];
    };

    CodeUtils.getValue = function(key, code) {
        return CodeUtils.codeTable[key][code];
    };

    return CodeUtils;
});
