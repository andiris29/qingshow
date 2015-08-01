// @formatter:off
define([
], function(
) {
// @formatter:on
    var StatusTo2Detail = function(ownerView) {
        this._ownerView = ownerView;
        this._ownerViewDom = this._ownerView.dom();
    };

    StatusTo2Detail.prototype.render = function() {
        $('.detail', this._ownerViewDom).hide();
        $('.actual', this._ownerViewDom).show();
    };

    StatusTo2Detail.prototype.getDetails = function() {
        var actualPrice = $('#actualPrice', this._ownerViewDom).val();
        if (!actualPrice) {
            alertify.error('需要输入实际价格');
            return;
        }
        return {
            'actualPrice' : actualPrice
        };
    };

    return StatusTo2Detail;
});
