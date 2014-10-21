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

    UIContainer.prototype.delegateEvent = function(eventName) {
        var delegation = function() {
            this.trigger(eventName);
        }.bind(this);

        this._children.forEach( function(child) {
            child.on(eventName, delegation);
        }.bind(this));
    };

    UIContainer.prototype.delegateFunction = function(functionName) {
        this[functionName] = function() {
            var args = arguments;
            this._children.forEach( function(child) {
                if (child[functionName]) {
                    child[functionName].apply(child, args);
                }
            }.bind(this));
        }.bind(this);
    };

    return UIContainer;
});
