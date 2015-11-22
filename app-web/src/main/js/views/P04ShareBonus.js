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

        } else {

        }

        $('.p04-download', this._dom).on('click', __services.downloadService.download);

        var canvas = $('.p04-circle-count-canvas', this._dom)[0];
        var context = canvas.getContext('2d');
        context.beginPath();
        context.arc(200, 150, 100, 0, Math.PI * 2, true);
        //不关闭路径路径会一直保留下去，当然也可以利用这个特点做出意想不到的效果
        context.closePath();
        context.fillStyle = 'rgba(0,255,0,0.25)';
        context.fill();

    };
    violet.oo.extend(P04ShareBonus, violet.ui.ViewBase);




    return P04ShareBonus;
});
