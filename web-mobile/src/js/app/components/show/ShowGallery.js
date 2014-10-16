// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/utils/RenderUtils'
], function(UIComponent, TemplateManager, RenderUtils) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var ShowGallery = function(dom, data) {
        ShowGallery.superclass.constructor.apply(this, arguments);

        this._feeding = data.feeding;
        this._tpltLi$ = null;

        async.parallel([ function(callback) {
            // Load template
            TemplateManager.load('show/show-gallery.html', function(err, content$) {
                content$.css('minHeight', this._dom$.height() + 'px');
                this._dom$.append(content$);

                callback(null);
            }.bind(this));
        }.bind(this), function(callback) {
            // Load template for list items
            TemplateManager.load('show/show-gallery-li.html', function(err, content$) {
                callback(null, content$);
            }.bind(this));
        }.bind(this), function(callback) {
            // Load data
            this._feeding(1, 100, callback);
        }.bind(this)], function(err, results) {
            this._tpltLi$ = results[1];
            this._render(results[2]);
        }.bind(this));
    };

    andrea.oo.extend(ShowGallery, UIComponent);

    ShowGallery.prototype._render = function(response) {
        ShowGallery.superclass._render.apply(this, arguments);

        var containers$ = $('.qsLiItemContainer', this._dom$);

        var shows = response.data.shows;
        shows.forEach( function(show, index) {
            var li$ = this._renderOne(this._tpltLi$.clone(), show);
            var targetContainer$;
            containers$.each(function(index, container) {
                if (!targetContainer$ || targetContainer$.children().length > $(container).children().length) {
                    targetContainer$ = $(container);
                }
            });
            li$.appendTo(targetContainer$);
        }.bind(this));
    };

    ShowGallery.prototype._renderOne = function(li$, show) {
        $('.qsShowCover', li$).attr('src', RenderUtils.imagePathToURL(show.cover));
        $('.qsPortrait', li$).css('background-image', RenderUtils.imagePathToBackground(show.modelRef.portrait));
        $('.qsName', li$).text(show.modelRef.name);
        // TODO
        $('.qsRole', li$).text('设计师');
        $('.qsHeight', li$).text(show.modelRef.height + 'cm');
        $('.qsWeight', li$).text(show.modelRef.weight + 'kg');
        $('.qsStatus', li$).text(show.modelRef.modelInfo.status);
        $('.qsNumFollowers', li$).text(show.numLike);

        $('.qsShowCover, .qsStatus', li$).on('click', function() {
            appRuntime.view.to('app/views/show/S03Show', {
                'show' : show
            });
        }.bind(this));
        $('.qsModel', li$).on('click', function() {
            appRuntime.view.to('app/views/producer/P02Model', {
                'model' : show.modelRef
            });
        }.bind(this));
        return li$;
    };

    return ShowGallery;
});
