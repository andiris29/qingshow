// @formatter:off
define([
], function() {
// @formatter:on

    /**
     *
     *
     */
    var CodeUtils = {};

    CodeUtils.codeTable = [];
    CodeUtils.codeTable['people.gender'] = [];
    CodeUtils.codeTable['people.gender'][0] = '男性';
    CodeUtils.codeTable['people.gender'][1] = '女性';
    CodeUtils.codeTable['people.hairType'] = [];
    CodeUtils.codeTable['people.hairType'][0] = '所有';
    CodeUtils.codeTable['people.hairType'][1] = '长发';
    CodeUtils.codeTable['people.hairType'][2] = '超长发';
    CodeUtils.codeTable['people.hairType'][3] = '中长发';

    CodeUtils.getCodes = function(key) {
        return CodeUtils.codeTable[key];
    };

    CodeUtils.getValue = function(key, code) {
        return CodeUtils.codeTable[key][code];
    };

    return CodeUtils;
});
