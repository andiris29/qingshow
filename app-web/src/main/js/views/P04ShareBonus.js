// @formatter:off
define([
], function(
    ) {
// @formatter:on
    var P04ShareBonus = function(dom, initOptions) {
        P04ShareBonus.superclass.constructor.apply(this, arguments);
        var create = initOptions;
        var now = new Date();
        if (create && (now - create) < 15 * 60 * 1000) {
            $('.p04-withdraw-content', this._dom).css({'display' : 'none'});
            $('.p04-waiting-content', this._dom).css({'display' : 'block'});
        } else {
            $('.p04-withdraw-content', this._dom).css({'display' : 'block'});
            $('.p04-waiting-content', this._dom).css({'display' : 'none'});
        }

        $('.p04-download', this._dom).on('click', __services.downloadService.download);
    };
    violet.oo.extend(P04ShareBonus, violet.ui.ViewBase);


    return P04ShareBonus;
});
