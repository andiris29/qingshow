// @formatter:off
define([
    'violet/utils/OOUtil',
    'violet/ui/core/UIComponent'
], function(OOUtil, UIComponent) {
// @formatter:on
    var ShowThumb = function(dom, options) {
        ShowThumb.superclass.constructor.apply(this, arguments);

        this._mongoShow = options.show;
        this._render();
    };
    OOUtil.extend(ShowThumb, UIComponent);

    ShowThumb.prototype._render = function() {
        this.$('.qs-poster').css('background-image', 'url(' + this._mongoShow.posters[0] + ')');
    };
    return ShowThumb;
});
