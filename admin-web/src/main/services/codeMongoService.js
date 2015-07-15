define([], function() {
    var codeMongoService = {};

    var _map = {
        'trade.status' : {
            0 : '未付款',
            1 : '已付款',
            2 : '已代购',
            3 : '已发货',
            5 : '确认收货（交易结束）',
            7 : '退货中',
            9 : '退货成功（交易结束）',
            10 : '退货失败（交易结束）',
            11 : '换货中',
            12 : '换货成功（交易结束）',
            13 : '换货失败（交易结束）',
            14 : '换货已发出',
            15 : '自动确认收货（交易结束）',
            16 : '二次退换货',
            17 : '退款成功'
        }
    };

    codeMongoService.toName = function(field, code) {
        return _map[field][code];
    };

    codeMongoService.toNameWithCode = function(field, code) {
        return _map[field][code] + '(' + code + ')';
    };

    return codeMongoService;
});
