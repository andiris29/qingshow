/**
 * Created by wxy325 on 15/9/10.
 */
var GoblinMainSlaver = module.exports;
var ItemSourceType = require('./../scheduler/ItemSourceType');

//淘宝与天猫暂时分为一类
var supportType = [
    ItemSourceType.Taobao | ItemSourceType.Tmall,
    ItemSourceType.Hm,
    ItemSourceType.Jamy
];
//TODO schedule, add config
GoblinMainSlaver.start = function (config) {

};

//TODO 外部主动触发slaver请求并爬取下一个item
GoblinMainSlaver.tregger = function () {

};

