// @formatter:off
define([
], function(
    ) {
// @formatter:on
    var P04ShareBonus = function(dom, initOptions) {
        P04ShareBonus.superclass.constructor.apply(this, arguments);
        this._initOptions = initOptions;
        this._createDate = Date.parse(this._initOptions.create);
        this._maxTimeInterval = 15 * 60 * 1000;

        this._canvas$ = $('.p04-circle-count-canvas', this._dom);
        this._canvas = this._canvas$[0];
        this._canvasContext = this._canvas.getContext('2d');

        $(window).resize( function() {
            this._resizeHandler();
        }.bind(this));

        this._updateDomDisplay();
        if (!this._canWithdraw() && !this._timerId) {
            this._timerId = setInterval(function(){
                this._updateCounterCircle();
            }.bind(this), 1000);
        }

        $('.p04-download', this._dom).on('click', __services.downloadService.download);
        $('.p04-circle-count-container', this._dom).on('click', this._didClickWithdraw.bind(this));
        $('.p04-withdraw-success-alert-OK', this._dom).on('click', this._didClickOkOfAlert.bind(this));

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

    P04ShareBonus.prototype._updateDomDisplay = function () {
        if (this._canWithdraw()) {
            $('.p04-waiting-container', this._dom).css('display', 'none');
            $('.p04-circle-count-remain-time-container', this._dom).css('display', 'none');
            $('.p04-request-complete-container', this._dom).css('display', 'block');
            $('.p04-circle-count-request-complete-container', this._dom).css('display', 'block');
        } else {
            $('.p04-waiting-container', this._dom).css('display', 'block');
            $('.p04-circle-count-remain-time-container', this._dom).css('display', 'block');
            $('.p04-request-complete-container', this._dom).css('display', 'none');
            $('.p04-circle-count-request-complete--container', this._dom).css('display', 'none');
        }
    };

    P04ShareBonus.prototype._updateCounterCircle = function() {
        var canvasContainer$ = $('.p04-circle-count-container', this._dom);
        var width = canvasContainer$.width();
        var height = canvasContainer$.height();
        var strokeWidth = 3;
        var radius = (width < height? width : height) / 2 - strokeWidth / 2;

        if (this._canWithdraw()) {
            this._updateDomDisplay();
            if (this._timerId) {
                clearInterval(this._timerId);
                this._timerId = null;
            }
            var context = this._canvasContext;
            context.clearRect(0, 0, width, height);
            context.beginPath();
            context.arc(width/2, height/2, radius, 0, Math.PI * 2, false);
            context.strokeStyle = 'rgba(38, 44, 89, 1)';
            context.lineWidth = strokeWidth;
            context.stroke();

            context.beginPath();
            context.arc(width/2, height/2 - radius, 3, 0, Math.PI * 2, false);
            context.fillStyle = 'rgba(38, 44, 89, 1)';
            context.fill();
        } else {
            var nowDate = new Date();
            var context = this._canvasContext;
            context.clearRect(0, 0, width, height);

            var begin = - Math.PI / 2;
            var end = begin + 2 * Math.PI * (nowDate - this._createDate) / this._maxTimeInterval;
            context.beginPath();
            context.arc(width/2, height/2, radius, begin, end, false);
            context.strokeStyle = 'rgba(38, 44, 89, 1)';
            context.lineWidth = strokeWidth;
            context.stroke();

            context.beginPath();
            context.arc(width/2, height/2, radius, begin, end, true);
            context.strokeStyle = 'rgba(201, 201, 201, 1)';
            context.lineWidth = strokeWidth;
            context.stroke();
            this._updateRemainTimeText(this._maxTimeInterval - (nowDate - this._createDate));
            context.beginPath();
            context.arc(width/2 + radius * Math.cos(end), height/2 + radius * Math.sin(end), 3, 0, Math.PI * 2, false);
            context.fillStyle = 'rgba(38, 44, 89, 1)';
            context.fill();

        }
    };
    P04ShareBonus.prototype._updateRemainTimeText = function (timeInterval) {
        var totalSeconds = parseInt(timeInterval/1000);
        var minute = parseInt(totalSeconds / 60);
        var seconds = parseInt(totalSeconds % 60);
        var displayStr = _toTwoDigitalsstr(minute) + ':' + _toTwoDigitalsstr(seconds);
        $('.p04-circle-count-remain-time-number', this._dom).text(displayStr);
    };
    function _toTwoDigitalsstr(number) {
        if (number < 10) {
            return '0' + number;
        } else {
            return '' + number;
        }
    }

    P04ShareBonus.prototype._canWithdraw = function () {
        var nowDate = new Date();
        return nowDate - this._createDate >= this._maxTimeInterval;
    };

    P04ShareBonus.prototype._didClickWithdraw = function () {
        if (this._canWithdraw()) {
            __services.httpService.request('/share/withdrawBonus', 'post', {
                '_id' : this._initOptions._id
            }, function(err, metadata, data) {
                if (!err) {
                    this._showAlert();
                } else {
                    //TODO Handle Error
                    console.log(err);
                }
            }.bind(this));
        }
    };

    P04ShareBonus.prototype._didClickOkOfAlert = function () {
        this._hideAlert();
    };

    P04ShareBonus.prototype._showAlert = function () {
        $('.p04-withdraw-success-alert', this._dom).css('display', 'block');
    };

    P04ShareBonus.prototype._hideAlert = function () {
        $('.p04-circle-count-container', this._dom).off('click');
        $('.p04-circle-count-request-complete-text', this._dom).text('提现完成');
        $('.p04-withdraw-success-alert', this._dom).css('display', 'none');
    };

    return P04ShareBonus;
});
