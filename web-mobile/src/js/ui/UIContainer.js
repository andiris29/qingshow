// @formatter:off
define([
    'ui/UIComponent'
], function(UIComponent) {
// @formatter:on
    var UIContainer = function(dom, data) {
        UIContainer.superclass.constructor.apply(this, arguments);

        this._children = [];
    };
    andrea.oo.extend(UIContainer, UIComponent);

    UIContainer.prototype.destroy = function() {
        this._children.forEach(function(child) {
            child.destroy();
        });
        UIContainer.superclass.destroy.apply(this, arguments);
    };

    UIContainer.prototype.append = function(uiComponent) {
        uiComponent.dom$().appendTo(this.dom$());
        this._children.push(uiComponent);
    };

    UIContainer.prototype.delegate = function(eventName) {
        this._children.forEach( function(child) {
            child.on(eventName, this.trigger(eventName));
        }.bind(this));
    };

    return UIContainer;
});
