// @formatter:off
define([
    'main/services/codeMongoService'
], function(
    codeMongoService
) {
// @formatter:on
    var TradeLi = function(dom, initOptions) {
        TradeLi.superclass.constructor.apply(this, arguments);

        var status = initOptions.status;
        var text = violet.string.substitute('订单管理－{0} ({1})', codeMongoService.toName('trade.status', status), status);
        $('#anchor', this._dom).text(text).on('click', function() {
            this._ownerView.push('/main/views/P03TradeList');
        }.bind(this));
    };
    violet.oo.extend(TradeLi, violet.ui.UIBase);

    return TradeLi;
});
