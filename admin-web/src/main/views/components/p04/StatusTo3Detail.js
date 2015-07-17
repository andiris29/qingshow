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
        $('.logistic', this._ownerViewDom).show();
    };

    StatusTo2Detail.prototype.getDetails = function() {
        var company = $('#company', this._ownerViewDom).val();
        if (!company) {
            alertify.error('需要输入快递公司');
            return;
        }
        var trackingID = $('#trackingID', this._ownerViewDom).val();
        if (!trackingID) {
            alertify.error('需要输入快递单号');
            return;
        }
        return {
            'logistic.company' : company,
            'logistic.trackingID' : trackingID
        };
    };

    return StatusTo2Detail;
});
