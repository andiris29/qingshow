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
            'click' : true,
            'probeType' : 3
        });
        this._iscroll.on('scrollEnd', function() {
            if (this._iscroll.y === this._iscroll.maxScrollY) {
                this._children.forEach(function(child) {
                    if (child.expand) {
                        child.expand();
                    };
                });
            }
        }.bind(this));
        this._iscroll.on('scroll', function() {
            this._dom$.trigger('scroll');
        }.bind(this));
    };
    andrea.oo.extend(IScrollContainer, UIContainer);

    /**
     *
     * @param {Object} uiComponent
     * uiComponent.expand will be invoke when scroll to bottom.
     */
    IScrollContainer.prototype.append = function(uiComponent) {
        this._children.push(uiComponent);

        uiComponent.dom$().appendTo(this._scroller$);
        uiComponent.on('afterRender resize', function() {
            this._refresh(uiComponent);
        }.bind(this));

        if ( uiComponent instanceof UIContainer) {
            uiComponent.delegateEvent('afterRender');
            uiComponent.delegateEvent('resize');
            uiComponent.delegateFunction('expand');
        }
        this._refresh(uiComponent);
    };

    IScrollContainer.prototype._refresh = function(child) {
        $('img.lazy:visible', child.dom$()).lazyload({
            'effect' : 'fadeIn',
            'container' : this._dom$,
            'failure_limit' : Number.POSITIVE_INFINITY
        }).removeClass('lazy');

        this._iscroll.refresh();
    };

    return IScrollContainer;
});
