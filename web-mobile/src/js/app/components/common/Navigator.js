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

        this._selectedChild = null;
    };
    andrea.oo.extend(Navigator, UIContainer);

    Navigator.prototype.append = function(uiComponent) {
        Navigator.superclass.append.apply(this, arguments);

        if (!this._selectedChild) {
            this.index(0);
        } else {
            uiComponent.dom$().hide();
        }
    };

    Navigator.prototype.index = function(value) {
        if (arguments.length > 0) {
            this._children.forEach( function(child, index) {
                if (value === index) {
                    child.dom$().show();
                    child.on('afterRender resize', this._refresh.bind(this));
                    this._selectedChild = child;
                    this._refresh();
                } else {
                    child.dom$().hide();
                    child.off();
                }
            }.bind(this));
        } else {
            return this._selectedChild ? this._selectedChild.dom$().index() : -1;
        }
    };

    /**
     * Integrate with IScrollContainer
     */
    Navigator.prototype.expand = function() {
        if (this._selectedChild.expand) {
            this._selectedChild.expand();
        }
    };

    Navigator.prototype._refresh = function() {
        this._dom$.height(this._selectedChild.dom$().height());
        this.trigger('resize');
    };

    return Navigator;
});
