// @formatter:off
define([
    'violet/utils/OOUtil',
    'violet/utils/InteractionUtil',
    'violet/ui/core/UIComponent'
], function(OOUtil, InteractionUtil, UIComponent) {
// @formatter:on
    var ThumbShow = function(dom, options) {
        ThumbShow.superclass.constructor.apply(this, arguments);

        this._mongoShow = options.show;

        this._render();

        InteractionUtil.onTouchOrClick(this._dom$, function(event) {
            this.trigger('requestOnStage');
        }.bind(this));
    };
    OOUtil.extend(ThumbShow, UIComponent);

    ThumbShow.prototype.mongoShow = function(value) {
        if (arguments.length) {
            this._mongoShow = value;
            this._render();
        } else {
            return this._mongoShow;
        }
    };

    ThumbShow.prototype._render = function() {
        this.$('.qs-poster').css('background-image', 'url(' + this._mongoShow.posters[0] + ')');
        var portrait$ = this.$('.qs-portrait');
        portrait$.css('background-image', 'url(' + this._mongoShow.modelRef.portrait + ')');
        portrait$.css({
            'height' : portrait$.width(),
            'border-radius' : portrait$.width() / 2
        });
    };
    return ThumbShow;
});
