// @formatter:off
define([
], function(
) {
// @formatter:on
    var P03ShareTrade = function(dom, initOptions) {
        P03ShareTrade.superclass.constructor.apply(this, arguments);

        __services.httpService.request('/trade/query', 'get', {
            '_ids' : [initOptions.entity._id]
        }, function(err, metadata, data) {
            if (data) {
                var trade = data.trades[0];
                var item = trade.itemSnapshot;

                $('.p03-trade-item-cover', this._dom).css('background-image', violet.string.substitute('url({0})', item.thumbnail));
                $('.p03-trade-item-title', this._dom)[0].innerText = item.name;
                $('.p03-trade-item-price-number', this._dom)[0].innerText = '￥' + parseFloat(item.price).toFixed(2);
                $('.p03-trade-item-tmall-price-number', this._dom)[0].innerText = '￥' + parseFloat(item.promoPrice).toFixed(2);

                //p03-trade-item-qingshow-discount-number
                //p03-trade-item-qingshow-price-number
                if (trade.actualPrice) {
                    var priceBefore = parseFloat(item.promoPrice || item.price);
                    var actualPrice = parseFloat(trade.actualPrice);
                    var discount = actualPrice * 100 / priceBefore;
                    $('.p03-trade-item-qingshow-discount-number', this._dom)[0].innerText = discount.toFixed(2) + '%';
                    $('.p03-trade-item-qingshow-price-number', this._dom)[0].innerText = '￥' + parseInt(actualPrice);
                }

            }
        }.bind(this));

        $('.p03-download', this._dom).on('click', __services.downloadService.download);
        $('.p03-success', this._dom).on('click', __services.downloadService.download);
    };
    violet.oo.extend(P03ShareTrade, violet.ui.ViewBase);

    return P03ShareTrade;
});
