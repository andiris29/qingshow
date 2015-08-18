// @formatter:off
define([
    'main/services/codeMongoService'
], function(
    codeMongoService
) {
// @formatter:on
    var TradeTr = function(dom, initOptions) {
        TradeTr.superclass.constructor.apply(this, arguments);

        var trade = initOptions.trade;

        var td$ = $('td', this._dom);
        td$.eq(0).text(trade._id);
        td$.eq(1).text(trade.itemSnapshot.name);
        td$.eq(2).text(trade.quantity);
        td$.eq(3).text(trade.totalFee);
        td$.eq(4).text(trade.peopleSnapshot ? trade.peopleSnapshot.nickname : 'invalid');
        td$.eq(5).text(codeMongoService.toNameWithCode('trade.status', trade.status));
        $('a', this._dom).on('click', function() {
            this._ownerView.push('main/views/P04EditTrade', initOptions);
        }.bind(this));
    };
    violet.oo.extend(TradeTr, violet.ui.UIBase);

    return TradeTr;
});
