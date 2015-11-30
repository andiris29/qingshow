// @formatter:off
define([
], function(
) {
// @formatter:on
    var StatusToEndDetail = function(ownerView) {
        this._ownerView = ownerView;
        this._ownerViewDom = this._ownerView.dom();
    };

    StatusToEndDetail.prototype.render = function() {
        $('.detail', this._ownerViewDom).hide();
        $('.comment', this._ownerViewDom).show();
    };

    StatusToEndDetail.prototype.getDetails = function() {
        var comment = $('#comment', this._ownerViewDom).val();
        if (!comment) {
            alertify.error('需要输入备注');
            return;
        }
        return {
            'adminNote' : comment
        };
    };

    return StatusToEndDetail;
});
