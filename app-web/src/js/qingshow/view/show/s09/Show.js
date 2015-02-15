// @formatter:off
define([
    'violet/utils/OOUtil',
    'violet/ui/core/UIComponent'
], function(OOUtil, UIComponent) {
// @formatter:on
    var Show = function(dom, options) {
        Show.superclass.constructor.apply(this, arguments);

        this._mongoShow = options.show;
        this._render();
    };
    OOUtil.extend(Show, UIComponent);

    Show.prototype._render = function() {
        this.$('.qs-poster').css('background-image', 'url(' + this._mongoShow.posters[0] + ')');
        var portrait$ = this.$('.qs-portrait');
        portrait$.css('background-image', 'url(' + this._mongoShow.modelRef.portrait + ')');
        portrait$.css({
            'height' : portrait$.width(),
            'border-radius' : portrait$.width() / 2
        });
    };

    return Show;
});
