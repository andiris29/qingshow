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
    var PeopleList = function(dom, data) {
        PeopleList.superclass.constructor.apply(this, arguments);

    };

    andrea.oo.extend(PeopleList, UIComponent);

    PeopleList.prototype.expand = function() {
    };

    return PeopleList;
});
