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
                    if (child.grow) {
                        child.grow();
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
     * uiComponent.grow will be invoke when scroll to bottom.
     */
    IScrollContainer.prototype.append = function(uiComponent) {
        this._children.push(uiComponent);

        uiComponent.dom$().appendTo(this._scroller$);
        uiComponent.on('resize', this._iscroll.refresh.bind(this._iscroll));
        uiComponent.on('afterRender', function() {
            this._afterRenderHandler(uiComponent);
        }.bind(this));
        this._afterRenderHandler(uiComponent);
    };
    IScrollContainer.prototype._afterRenderHandler = function(child) {
        $('img.lazy', child.dom$()).lazyload({
            'effect' : 'fadeIn',
            'container' : this._dom$,
            'failure_limit' : Number.POSITIVE_INFINITY
        }).removeClass('lazy');

        this._iscroll.refresh();
    };

    return IScrollContainer;
});
