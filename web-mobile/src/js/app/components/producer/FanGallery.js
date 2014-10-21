// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/utils/CodeUtils',
    'app/utils/RenderUtils'
], function(UIComponent, TemplateManager, CodeUtils, RenderUtils) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var FanGallery = function(dom, data) {
        FanGallery.superclass.constructor.apply(this, arguments);

    };

    andrea.oo.extend(FanGallery, UIComponent);

    FanGallery.prototype.expand = function() {
    };

    return FanGallery;
});
