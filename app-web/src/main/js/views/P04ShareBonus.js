// @formatter:off
define([
], function(
    ) {
// @formatter:on
    var P04ShareBonus = function(dom, initOptions) {
        P04ShareBonus.superclass.constructor.apply(this, arguments);

        this._canvas$ = $('.p04-circle-count-canvas', this._dom);
        this._canvas = this._canvas$[0];
        this._canvasContext = this._canvas.getContext('2d');

        $(window).resize( function() {
            this._resizeHandler();
        }.bind(this));
        this._resizeHandler();


        var create = initOptions;

        this._createDate = create;
        this._createDate = new Date(); //TODO for test
        this._maxTimeInterval = 15 * 60 * 1000;
        if (this._canWithdraw()) {

        } else {
            $('.p04-waiting-container', this._dom).css('display', 'block');
            $('.p04-circle-count-remain-time-container', this._dom).css('display', 'block');

            setInterval(function(){
                this._updateCounterCircle();
            }.bind(this), 1000);
        }

        $('.p04-download', this._dom).on('click', __services.downloadService.download);

        setTimeout(function(){
            this._resizeHandler();
        }.bind(this), 0);


    };
    violet.oo.extend(P04ShareBonus, violet.ui.ViewBase);

    P04ShareBonus.prototype._resizeHandler = function() {
        var canvasContainer$ = $('.p04-circle-count-container', this._dom);
        var width = canvasContainer$.width();
        var height = canvasContainer$.height();
        this._canvas$.attr({
            width : width,
            height : height
        });
        this._updateCounterCircle();
    };

    P04ShareBonus.prototype._updateCounterCircle = function() {
        var canvasContainer$ = $('.p04-circle-count-container', this._dom);
        var width = canvasContainer$.width();
        var height = canvasContainer$.height();
        var strokeWidth = 3;
        var radius = (width < height? width : height) / 2 - strokeWidth / 2;


        if (this._canWithdraw()) {
            var nowDate = new Date();
            var context = this._canvasContext;
            context.clearRect(0, 0, width, height);

            var begin = - Math.PI / 2;
            var end = begin + 2 * Math.PI * (nowDate - this._createDate) / this._maxTimeInterval;

            context.beginPath();
            context.arc(width/2, height/2, radius, begin, end, false);
            context.strokeStyle = 'rgba(200, 16, 78, 1)';
            context.lineWidth = strokeWidth;
            context.stroke();

            context.beginPath();
            context.arc(width/2, height/2, radius, begin, end, true);
            context.strokeStyle = 'rgba(201, 201, 201, 1)';
            context.lineWidth = strokeWidth;

            context.stroke();

        } else {

        }
    };
    P04ShareBonus.prototype._updateRemainTimeText = function () {

    };

    P04ShareBonus.prototype._canWithdraw = function () {
        var nowDate = new Date();
        return nowDate - this._createDate >= this._maxTimeInterval;
    };


    return P04ShareBonus;
});
