// @formatter:off
define([
    'main/services/codeMongoService'
], function(
    codeMongoService
) {
// @formatter:on
    var TradeStatusLi = function(dom, initOptions) {
        TradeStatusLi.superclass.constructor.apply(this, arguments);

        var status = initOptions.status;
        var text = violet.string.substitute('订单管理－{0}', codeMongoService.toNameWithCode('trade.status', status));
        $('#anchor', this._dom).text(text).on('click', function() {
            this._ownerView.push('main/views/P03TradeList', initOptions);
        }.bind(this));
    };
    violet.oo.extend(TradeStatusLi, violet.ui.UIBase);

    return TradeStatusLi;
});
