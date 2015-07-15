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
        $('.taobaoInfo', this._ownerViewDom).show();
    };

    StatusTo2Detail.prototype.getDetails = function() {
        var userNick = $('#userNick', this._ownerViewDom).val();
        if (!userNick) {
            alertify.error('需要输入淘宝用户');
            return;
        }
        var tradeID = $('#tradeID', this._ownerViewDom).val();
        if (!tradeID) {
            alertify.error('需要输入淘宝单号');
            return;
        }
        return {
            'userNick' : userNick,
            'tradeID' : tradeID
        };
    };

    return StatusTo2Detail;
});
