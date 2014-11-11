// @formatter:off
define([
    'ui/containers/IScrollContainer',
    'app/views/ViewBase',
    'app/components/common/Header',
    'app/components/show/Show'
], function(IScrollContainer, ViewBase, Header, Show) {
// @formatter:on
    /**
     * data
     *      show An _id/entity of shows
     */
    var S03Show = function(dom, data) {
        S03Show.superclass.constructor.apply(this, arguments);

        var header = new Header($('<div/>').appendTo(this._dom$));
        var body = new IScrollContainer($('<div/>').css({
            'width' : '100%',
            'height' : this._dom$.height() - header.getPreferredSize().height
        }).appendTo(this._dom$));

        var main = this._main = new Show($('<div/>'), data.show);
        body.append(main);

        this.on('destroying', function() {
            main.dispose();
        });
    };
    andrea.oo.extend(S03Show, ViewBase);

    S03Show.prototype.deactivate = function() {
        S03Show.superclass.deactivate.apply(this, arguments);

        this._main.pause();
    };

    return S03Show;
});
