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
        
        // Mac chrome
        // iscroll vs native scroll @1000 shows => 25 vs 20 fps (iscroll)
        // probeType 3 + lazyload vs probeType 1 + lazyload when scrollEnd@1000 shows => 1 vs 25? (TODO)
        
        // enable useTransform will cause font rendered not clearly, and disable it will cause performance loss.
        // Scroll fps will loss 30% both desktop chrome and mobile safari. Hard to choose...
        this._iscroll = new IScroll(this.dom(), {
            'mouseWheel' : true,
            'tap' : 'iscrollTap',
            // TODO This is for lazy load image, but have impact on the cpu. Remove this option and lazyload when scrollEnd.
            'probeType' : 3,
            'useTransform' : false
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
            // 'effect' : 'fadeIn',
            'container' : this._dom$,
            'failure_limit' : Number.POSITIVE_INFINITY
        }).removeClass('lazy');
        this._iscroll.refresh();
    };

    return IScrollContainer;
});
