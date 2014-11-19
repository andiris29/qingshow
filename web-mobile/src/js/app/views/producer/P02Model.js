// @formatter:off
define([
    'ui/containers/IScrollContainer',
    'app/services/FeedingService',
    'app/views/ViewBase',
    'app/components/common/Header',
    'app/components/common/Navigator',
    'app/components/producer/Model',
    'app/components/producer/FanGallery',
    'app/components/show/ShowGallery'
], function(IScrollContainer, FeedingService, ViewBase, Header, Navigator, Model, FanGallery, ShowGallery) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var P02Model = function(dom, data) {
        P02Model.superclass.constructor.apply(this, arguments);
        this._model = data.model;

        var header = new Header($('<div/>').appendTo(this._dom$));

        var body = new IScrollContainer($('<div/>').css({
            'width' : '100%',
            'height' : this._dom$.height() - header.getPreferredSize().height
        }).appendTo(this._dom$));

        var model = new Model($('<div/>'), this._model);
        body.append(model);

        var navi = new Navigator($('<div/>'));
        var byModel, byFollow;
        navi.append( byModel = new ShowGallery($('<div/>'), {
            'feeding' : function(pageNo, callback) {
                FeedingService.byModel(this._model._id, pageNo, callback);
            }.bind(this)
        }));
        navi.append( byFollow = new ShowGallery($('<div/>'), {
            'feeding' : function(pageNo, callback) {
                // FeedingService.byFollow(this._model._id, pageNo, callback);
            }.bind(this)
        }));
        navi.append(new FanGallery($('<div/>'), {
        }));
        body.append(navi);
        //
        model.on('selectTab', function(event, index) {
            navi.index(index);
        });
        byModel.on('afterRender', function(event) {
            model.numShows(byModel.numTotal());
        });
        byFollow.on('afterRender', function(event) {
            model.numShowsFollow(byFollow.numTotal());
        });
    };
    andrea.oo.extend(P02Model, ViewBase);

    return P02Model;
});
