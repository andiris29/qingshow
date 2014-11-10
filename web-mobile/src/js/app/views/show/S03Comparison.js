// @formatter:off
define([
    'ui/containers/IScrollContainer',
    'app/services/FeedingService',
    'app/views/ViewBase',
    'app/components/common/Header',
    'app/components/show/ComparisonNavi',
    'app/components/show/ComparisonGallery'
], function(IScrollContainer, FeedingService, ViewBase, Header, ComparisonNavi, ComparisonGallery) {
// @formatter:on
    /**
     * TODO
     */
    var S03Comparison = function(dom, data) {
        S03Comparison.superclass.constructor.apply(this, arguments);
        this._feeding = data.feeding;

        var header = new Header($('<div/>').appendTo(this._dom$), {
            'title' : data.title
        });
        var body = new IScrollContainer($('<div/>').css({
            'width' : '100%',
            'height' : this._dom$.height() - header.getPreferredSize().height
        }).appendTo(this._dom$));

        body.append(new ComparisonNavi($('<div/>')));
        body.append(new ComparisonGallery($('<div/>'), {
            'feeding' : FeedingService.chosenByEditor
        }));
    };
    andrea.oo.extend(S03Comparison, ViewBase);

    return S03Comparison;
});
