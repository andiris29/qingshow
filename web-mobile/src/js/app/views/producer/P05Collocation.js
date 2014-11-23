// @formatter:off
define([
    'ui/containers/IScrollContainer',
    'app/model',
    'app/services/UserService',
    'app/services/QueryService',
    'app/views/ViewBase',
    'app/components/common/Header',
    'app/components/common/Navigator',
    'app/components/producer/Collocated',
    'app/components/producer/Collocation',
    'app/components/producer/PItemGallery'
], function(IScrollContainer, model, UserService, QueryService, ViewBase, Header, Navigator, Collocated, Collocation, PItemGallery) {
// @formatter:on
    /**
     *
     */
    var P05Collocation = function(dom, data) {
        P05Collocation.superclass.constructor.apply(this, arguments);

        this.loginRequired = true;
        // Header
        var header = new Header($('<div/>').appendTo(this._dom$), {
            'left' : null,
            'right' : '登出'
        });
        header.on('clickRight', function() {
            UserService.logout(function() {
                appRuntime.view.to('app/views/user/U06Login');
            });
        });
        var collocated = this._collocated = new Collocated($('<div/>').appendTo(this._dom$));
        // Body
        var body = new IScrollContainer($('<div/>').css({
            'width' : '100%',
            'height' : this._dom$.height() - header.getPreferredSize().height - collocated.getPreferredSize().height
        }).appendTo(this._dom$));

        var collocation = new Collocation($('<div/>'));
        body.append(collocation);

        var navi = new Navigator($('<div/>'));
        _appendGallery(navi, collocated, [0], collocation.numUppers);
        _appendGallery(navi, collocated, [1], collocation.numLowers);
        _appendGallery(navi, collocated, [2], collocation.numShoes);
        _appendGallery(navi, collocated, [3], collocation.numAccessories);

        body.append(navi);
        //
        collocation.on('selectTab', function(event, index) {
            navi.index(index);
        });
    };
    andrea.oo.extend(P05Collocation, ViewBase);

    _appendGallery = function(navi, collocated, categories, numTotalRenderer) {
        var gallery = new PItemGallery($('<div/>'), {
            'query' : function(pageNo, callback) {
                QueryService.pItemsByCategories(categories, pageNo, callback);
            }.bind(this)
        });
        navi.append(gallery);
        gallery.on('afterRender', function(event) {
            numTotalRenderer(gallery.numTotal());
        });
        gallery.on('collocate', function(event, pItem) {
            collocated.collocate(pItem);
        });
        gallery.on('uncollocate', function(event, pItem) {
            collocated.uncollocate(pItem);
        });
        collocated.on('save', gallery.reset.bind(gallery));
        collocated.on('uncollocate', function(event, pItem) {
            gallery.uncollocate(pItem);
        });
        model.on('userChanged', gallery.reset.bind(gallery));
        return gallery;
    };

    P05Collocation.prototype.activate = function() {
        P05Collocation.superclass.activate.apply(this, arguments);

        this._collocated.reset();
    };

    return P05Collocation;
});
