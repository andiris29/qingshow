// @formatter:off
define([
    'ui/containers/IScrollContainer',
    'ui/containers/SlickContainer',
    'app/services/FeedingService',
    'app/views/ViewBase',
    'app/components/header/CommonHeader',
    'app/components/producer/Model',
    'app/components/show/ShowGallery'
], function(IScrollContainer, SlickContainer, FeedingService, ViewBase, CommonHeader, Model, ShowGallery) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var P02Model = function(dom, data) {
        P02Model.superclass.constructor.apply(this, arguments);
        this._model = data.model;

        var header = new CommonHeader($('<div/>').appendTo(this._dom$));
        var body = new IScrollContainer($('<div/>').css({
            'width' : '100%',
            'height' : this._dom$.height() - header.getPreferredSize().height
        }).appendTo(this._dom$));

        var model = new Model($('<div/>'), this._model);
        body.append(model);

        var slickContainer = new SlickContainer($('<div/>'), {
            'options' : {
                'slidesToShow' : 1,
                'infinite' : false,
                'adaptiveHeight' : true
            }
        });
        slickContainer.append(new ShowGallery($('<div/>'), {
            'feeding' : FeedingService.choosen
        }));
        body.append(slickContainer);
    };
    andrea.oo.extend(P02Model, ViewBase);

    return P02Model;
});
