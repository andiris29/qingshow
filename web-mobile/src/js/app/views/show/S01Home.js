// @formatter:off
define([
    'ui/containers/IScrollContainer',
    'app/services/FeedingService',
    'app/views/ViewBase',
    'app/components/common/Header',
    'app/components/show/ShowGallery',
], function(IScrollContainer, FeedingService, ViewBase, Header, ShowGallery) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var S01Home = function(dom) {
        S01Home.superclass.constructor.apply(this, arguments);

        var header = new Header($('<div/>').appendTo(this._dom$), {
            'left' : Header.BUTTON_MENU
        });
        var body = new IScrollContainer($('<div/>').css({
            'width' : '100%',
            'height' : this._dom$.height() - header.getPreferredSize().height
        }).appendTo(this._dom$));

        var gallery = new ShowGallery($('<div/>'), {
            'feeding' : FeedingService.chosen
        });
        body.append(gallery);
    };
    andrea.oo.extend(S01Home, ViewBase);

    return S01Home;
});
