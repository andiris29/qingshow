// @formatter:off
define([
    'main/services/codeMongoService'
], function(
    codeMongoService
) {
// @formatter:on
    var ItemPriceChangedLi = function(dom, initOptions) {
        ItemPriceChangedLi.superclass.constructor.apply(this, arguments);

        $('#anchor', this._dom).on('click', function() {
            alertify.prompt('输入降价商品 _id', function(result, _id) {
                if (_id) {
                    alertify.prompt(violet.string.substitute('输入 {0} 的价格', _id), function(result, actualPrice) {
                        if (actualPrice) {
                            this._ownerView.request('/notify/itemPriceChanged', 'post', {
                                '_id' : _id,
                                'actualPrice' : actualPrice
                            }, function(err, metadata, data) {
                                if (err || metadata.error) {
                                    alertify.success('推送失败');
                                } else {
                                    alertify.success('推送成功');
                                }
                            }.bind(this));
                        }
                    }.bind(this));
                }
            }.bind(this));
        }.bind(this));
    };
    violet.oo.extend(ItemPriceChangedLi, violet.ui.UIBase);

    return ItemPriceChangedLi;
});
