// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/services/DataService',
    'app/utils/RenderUtils'
], function(UIComponent, TemplateManager, DataService, RenderUtils) {
// @formatter:on
  
    var HairTypeComponent = function(dom, people) {
        HairTypeComponent.superclass.constructor.apply(this, arguments);

        async.parallel([function(callback) {
        }.bind(this)], function(callback) {
            this._render(people);
        }.bind(this));

    };

    andrea.oo.extend(HairTypeComponent, UIComponent);

    HairTypeComponent.prototype._render = function(people) {
    };

    HairTypeComponent.prototype.save = function() {
    };
});
