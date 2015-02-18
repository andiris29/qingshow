// @formatter:off
define([
    'violet/utils/OOUtil',
    'violet/ui/core/UIComponent'
], function(OOUtil, UIComponent) {
// @formatter:on
    var UIContainer = function(dom, data) {
        UIContainer.superclass.constructor.apply(this, arguments);

        // Array<UIComponent>
        this._children = [];
    };
    OOUtil.extend(UIContainer, UIComponent);

    UIContainer.prototype.destroy = function() {
        this._children.forEach(function(child) {
            child.destroy();
        });
        UIContainer.superclass.destroy.apply(this, arguments);
    };

    UIContainer.prototype.addChild = function(uiComponent) {
        uiComponent.dom$().appendTo(this.dom$());
        this._children.push(uiComponent);
    };

    UIContainer.prototype._delegateEvent = function(eventName) {
        var delegation = function() {
            this.trigger(eventName);
        }.bind(this);

        this._children.forEach( function(child) {
            child.on(eventName, delegation);
        }.bind(this));
    };

    UIContainer.prototype._delegateFunction = function(functionName) {
        if (this[functionName]) {
            return;
        }
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
