// @formatter:off
define([
    'ui/containers/IScrollContainer',
    'app/views/ViewBase',
    'app/components/header/CommonHeader',
    'app/components/show/ShowGallery'
], function(IScrollContainer, ViewBase, CommonHeader, ShowGallery) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var S02Feeding = function(dom, data) {
        S02Feeding.superclass.constructor.apply(this, arguments);
        this._feeding = data.feeding;

        var header = new CommonHeader($('<div/>').appendTo(this._dom$), '分类 xxx');
        var body = new IScrollContainer($('<div/>').css({
            'width' : '100%',
            'height' : this._dom$.height() - header.getPreferredSize().height
        }).appendTo(this._dom$));

        var gallery = new ShowGallery($('<div/>'), {
            'feeding' : this._feeding
        });
        body.append(gallery);
    };
    andrea.oo.extend(S02Feeding, ViewBase);

    return S02Feeding;
});
