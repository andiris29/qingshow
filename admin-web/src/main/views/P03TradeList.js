// @formatter:off
define([
    'main/views/View',
    'main/services/codeMongoService'
], function(
    View,
    codeMongoService
) {
    violet.ui.factory.registerDependencies('main/views/P03TradeList', [
        'main/views/components/p03/TradeTr']);
// @formatter:on
    var P03TradeList = function(dom, initOptions) {
        P03TradeList.superclass.constructor.apply(this, arguments);

        this.request('/admin/find', 'get', {
            'collection' : 'trades',
            'status' : initOptions.status
        }, function(err, metadata, data) {
            if (err || metadata.error) {
                alertify.error(violet.string.substitute('不存在{0} ({1})的交易', codeMongoService.toName('trade.status', initOptions.status), initOptions.status));
                return;
            }
            var parent$ = $('tbody', this._dom);
            data.models.forEach( function(trade) {
                violet.ui.factory.createUi('main/views/components/p03/TradeTr', {
                    'trade' : trade
                }, parent$, this);
            }.bind(this));
        }.bind(this));
    };

    violet.oo.extend(P03TradeList, View);

    return P03TradeList;
});
