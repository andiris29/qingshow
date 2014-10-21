// @formatter:off
define([
    'ui/UIContainer'
], function(UIContainer) {
// @formatter:on
    /**
     */
    var Navigator = function(dom) {
        Navigator.superclass.constructor.apply(this, arguments);

        this._dom$.addClass('qsNavigator');

        this._animating = false;
        this._index = -1;
    };
    andrea.oo.extend(Navigator, UIContainer);

    Navigator.prototype.append = function(uiComponent) {
        Navigator.superclass.append.apply(this, arguments);

        if (this._index === -1) {
            this.index(0);
        } else {
            uiComponent.dom$().hide();
        }
    };

    Navigator.prototype.index = function(value) {
        if (arguments.length > 0) {
            value = Math.min(Math.max(value, 0), this._children.length - 1);
            if (this._index === value) {
                return;
            }
            var currentChild = this._children[this._index], child = this._children[value];
            var animation = value > this._index ? PageTransitions.animations.nextTab : PageTransitions.animations.prevTab;
            this._index = value;
            this._activateChild(child);
            if (this._children.length === 1) {
                this._refresh();
            } else {
                this._animating = true;
                new PageTransitions(currentChild.dom$(), this._children[value].dom$()).nextPage(animation, function() {
                    this._animating = false;
                    this._deactivateChild(currentChild);
                    this._refresh();
                }.bind(this));
            }
        } else {
            return this._index;
        }
    };

    /**
     * Integrate with IScrollContainer
     */
    Navigator.prototype.expand = function() {
        var child = this._children[this._index];
        if (child && child.expand) {
            child.expand();
        }
    };

    Navigator.prototype._activateChild = function(child) {
        child.dom$().show();
        child.on('afterRender resize', this._refresh.bind(this));
    };

    Navigator.prototype._deactivateChild = function(child) {
        child.dom$().hide();
        // child.off();
    };

    Navigator.prototype._refresh = function() {
        var child = this._children[this._index];
        this._dom$.height(child.dom$().height());
        this.trigger('resize');
    };

    return Navigator;
});
