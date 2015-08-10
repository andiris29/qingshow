// @formatter:off
define([
], function(
) {
// @formatter:on
    var StatusTo3Detail = function(ownerView) {
        this._ownerView = ownerView;
        this._ownerViewDom = this._ownerView.dom();
    };

    StatusTo3Detail.prototype.render = function() {
        $('.detail', this._ownerViewDom).hide();
        $('.logistic', this._ownerViewDom).show();
    };

    StatusTo3Detail.prototype.getDetails = function() {
        var company = $('#company', this._ownerViewDom).val();
        if (!company) {
            alertify.error('需要输入快递公司');
            return;
        }
        var trackingId = $('#trackingId', this._ownerViewDom).val();
        if (!trackingId) {
            alertify.error('需要输入快递单号');
            return;
        }
        return {
            'logistic' : {
                'company' : company,
                'trackingId' : trackingId
            }
        };
    };

    return StatusTo3Detail;
});
