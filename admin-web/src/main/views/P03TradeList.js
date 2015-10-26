// @formatter:off
define([
    'main/core/model',
    'main/views/View',
    'main/services/codeMongoService'
], function(
    model,
    View,
    codeMongoService
) {
    violet.ui.factory.registerDependencies('main/views/P03TradeList', [
        'main/views/components/p03/TradeTr']);
// @formatter:on
    var P03TradeList = function(dom, initOptions) {
        P03TradeList.superclass.constructor.apply(this, arguments);

        this._refresh();
        model.on('trades.changed', this._refresh.bind(this));
    };

    violet.oo.extend(P03TradeList, View);

    P03TradeList.prototype.destroy = function() {
        model.off('trades.changed');
        
        P03TradeList.superclass.destroy.apply(this, arguments);
    };
    
    P03TradeList.prototype._refresh = function() {
        var status = this._initOptions.status;
        var parent$ = $('tbody', this._dom).empty();

        this.request('/admin/find', 'get', {
            'collection' : 'trades',
            'pageSize' : 1000,
            'status' : status
        }, function(err, metadata, data) {
            if (err || metadata.error) {
                alertify.error(violet.string.substitute('不存在{0}的交易', codeMongoService.toNameWithCode('trade.status', status)));
                return;
            }
            var models = [];
            if(status === 2){
                models = data.models.filter(function(trade) {
                    return !trade.pay.forge;
                }).sort(function(a, b){
                    if(b.highlight > a.highlight){
                        return 1;
                    }else if(b.highlight < a.highlight){
                        return -1;
                    }else{
                        return 0;
                    }
                });
            }else {
                models = data.models;
            }

            models.forEach( function(trade) {
                violet.ui.factory.createUi('main/views/components/p03/TradeTr', {
                    'trade' : trade
                }, parent$, this);
            }.bind(this));
        }.bind(this));
    };

    return P03TradeList;
});
