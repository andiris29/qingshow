// @formatter:off
define([
    'ui/scroll/IScrollContainer',
    'app/views/ViewBase',
    'app/components/header/CommonHeader',
    'app/components/show/ShowGallery'
], function(IScrollContainer, ViewBase, CommonHeader, ItemGallery) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var S02TagRecommendation = function(dom) {
        S02TagRecommendation.superclass.constructor.apply(this, arguments);

        var header = new CommonHeader($('<div/>').appendTo(this._dom$), '分类 xxx');
        var body = new IScrollContainer($('<div/>').css({
            'width' : '100%',
            'height' : this._dom$.height() - header.getPreferredSize().height
        }).appendTo(this._dom$));

        var gallery = new ItemGallery($('<div/>'));
        body.append(gallery);
    };
    andrea.oo.extend(S02TagRecommendation, ViewBase);

    return S02TagRecommendation;
});
