// @formatter:off
define([
], function(
) {
// @formatter:on
    var StatusTo1Detail = function(ownerView) {
        this._ownerView = ownerView;
        this._ownerViewDom = this._ownerView.dom();
    };

    StatusTo1Detail.prototype.render = function() {
        $('.detail', this._ownerViewDom).hide();
        $('.actual', this._ownerViewDom).show();
    };

    StatusTo1Detail.prototype.getDetails = function() {
        var actualPrice = $('#actualPrice', this._ownerViewDom).val();
        if (!actualPrice || isNaN(actualPrice)) {
            alertify.error('需要输入实际价格');
            return;
        }
        return {
            orders : [{
                'actualPrice' : parseFloat(actualPrice)
            }]
        };
    };

    return StatusTo1Detail;
});
