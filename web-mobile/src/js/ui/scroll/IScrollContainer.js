// @formatter:off
define([
    'ui/UIContainer'
], function(UIContainer) {
// @formatter:on
    var IScrollContainer = function(dom, data) {
        IScrollContainer.superclass.constructor.apply(this, arguments);

        this._dom$.addClass('uiIScrollWrapper').css({
            'position' : 'absolute',
            'overflow' : 'hidden'
        });
        this._scroller$ = $('<div/>').addClass('uiIScroller').css({
            'position' : 'absolute'
        }).appendTo(this._dom$);

        this._iscroll = null;

        this._refreshIScroll();
    };
    andrea.oo.extend(IScrollContainer, UIContainer);

    IScrollContainer.prototype._refreshIScroll = function(uiComponent) {
        if (this._iscroll) {
            this._iscroll.refresh();
        } else {
            this._iscroll = new IScroll(this.dom(), {
                'mouseWheel' : true,
                'click' : true
            });
        }
    };

    IScrollContainer.prototype.append = function(uiComponent) {
        uiComponent.dom$().appendTo(this._scroller$);

        uiComponent.on('resize', this._refreshIScroll.bind(this));
        this._refreshIScroll();

        this._children.push(uiComponent);
    };

    return IScrollContainer;
});
