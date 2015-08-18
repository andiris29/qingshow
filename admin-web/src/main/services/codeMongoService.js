define([], function() {
    var codeMongoService = {};

    var _map = {
        'trade.status' : {
            0 : '折扣申请中',
            1 : '等待付款',
            2 : '已付款',
            3 : '已发货',
            5 : '确认收货^',
            7 : '退货中',
            9 : '退货成功^',
            10 : '退货失败^',
            15 : '自动确认收货^',
            17 : '交易终止^',
            18 : '取消订单^'
        },
        'show.recommend.group' : {
            'A1' : '偏瘦型',
            'A2' : '标准型',
            'A3' : '偏胖型',
            'A4' : '超胖型'
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
