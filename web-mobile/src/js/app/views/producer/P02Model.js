// @formatter:off
define([
    'ui/scroll/IScrollContainer',
    'app/services/FeedingService',
    'app/views/ViewBase',
    'app/components/header/CommonHeader',
    'app/components/producer/ModelInfo',
    'app/components/show/ShowGallery'
], function(IScrollContainer, FeedingService, ViewBase, CommonHeader, ModelInfo, ShowGallery) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var P02Model = function(dom, data) {
        P02Model.superclass.constructor.apply(this, arguments);
        this._model = data.model;

        var header = new CommonHeader($('<div/>').appendTo(this._dom$), this._model.name);
        var modelInfo = new ModelInfo($('<div/>').appendTo(this._dom$), this._model);
        var body = new IScrollContainer($('<div/>').css({
            'width' : '100%',
            'height' : this._dom$.height() - header.getPreferredSize().height - modelInfo.getPreferredSize().height
        }).appendTo(this._dom$));

        var gallery = new ShowGallery($('<div/>'), {
            'feeding' : FeedingService.choosen
        });
        body.append(gallery);
    };
    andrea.oo.extend(P02Model, ViewBase);

    return P02Model;
});
