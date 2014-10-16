// @formatter:off
define([
    'ui/UIContainer'
], function(UIContainer) {
// @formatter:on
    /**
     *
     * @param {Object} dom
     */
    var IScrollContainer = function(dom) {
        IScrollContainer.superclass.constructor.apply(this, arguments);

        this._dom$.addClass('uiIScrollWrapper').css({
            'position' : 'absolute',
            'overflow' : 'hidden'
        });
        this._scroller$ = $('<div/>').addClass('uiIScroller').css({
            'position' : 'absolute'
        }).appendTo(this._dom$);

        this._iscroll = new IScroll(this.dom(), {
            'mouseWheel' : true,
            'click' : true
        });
        this._iscroll.on('scrollEnd', function() {
            if (this._iscroll.y === this._iscroll.maxScrollY) {
                this._children.forEach(function(child) {
                    if (child.grow) {
                        child.grow();
                    };
                });
            }
        }.bind(this));
    };
    andrea.oo.extend(IScrollContainer, UIContainer);

    /**
     *
     * @param {Object} uiComponent
     * uiComponent.grow will be invoke when scroll to bottom.
     */
    IScrollContainer.prototype.append = function(uiComponent) {
        uiComponent.dom$().appendTo(this._scroller$);

        uiComponent.on('resize', this._iscroll.refresh.bind(this._iscroll));
        this._iscroll.refresh();

        this._children.push(uiComponent);
    };

    return IScrollContainer;
});
