// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/services/FeedingService',
    'app/utils/CodeUtils',
    'app/utils/RenderUtils'
], function(UIComponent, TemplateManager, FeedingService, CodeUtils, RenderUtils) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var ShowGallery = function(dom, data) {
        ShowGallery.superclass.constructor.apply(this, arguments);

        this._feeding = data.feeding;
        this._pageNo = 1;
        this._numTotal = 0;

        this._expandable = true;

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
        }.bind(this)], function(err, results) {
            this._tpltLi$ = results[1];
            // Load data
            this._feeding(this._pageNo, this._render.bind(this));
        }.bind(this));
    };

    andrea.oo.extend(ShowGallery, UIComponent);

    ShowGallery.prototype.expand = function() {
        if (!this._expandable) {
            return;
        }
        $('.qsLoading .qsSpin', this._dom$).fadeIn();
        $('.qsLoading .qsText', this._dom$).text('努力加载中…');

        this._pageNo++;
        this._feeding(this._pageNo, this._render.bind(this));
    };

    ShowGallery.prototype.numTotal = function() {
        return this._numTotal;
    };

    ShowGallery.prototype._render = function(metadata, data) {
        ShowGallery.superclass._render.apply(this, arguments);

        $('.qsLoading .qsSpin', this._dom$).hide();
        if (metadata.error) {
            this._expandable = false;

            if (this._pageNo === 1) {
                $('.qsRefreshInfo', this._dom$).hide();
            }
        } else {
            this._numTotal = metadata.numTotal;
            this._expandable = (FeedingService.PAGE_SIZE * this._pageNo) < this._numTotal;

            // Refresh time
            if (metadata.refreshTime) {
                $('.qsRefreshInfo', this._dom$).show();
                var rm = moment(metadata.refreshTime);
                $('.qsHM', this._dom$).text(rm.format('HH:mm'));
                $('.qsDate', this._dom$).text(rm.format('YYYY/MM/DD'));
                $('.qsDay', this._dom$).text(rm.format('dddd'));
            } else {
                $('.qsRefreshInfo', this._dom$).hide();
            }
            // Shows
            var containers$ = $('.qsLiItemContainer', this._dom$);
            var shows = data.shows;
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
        }
        if (this._expandable) {
            $('.qsLoading .qsText', this._dom$).text('加载更多…');
        } else {
            $('.qsLoading .qsText', this._dom$).text('没有更多了…');
        }
    };

    // TODO performace optimize
    // Use an placeholder to occupy the height of lis which are not visible for each left & right
    // before, 200 li, 30fps@chrome
    // after, 200 li, 60fps@chrome

    ShowGallery.prototype._renderOne = function(li$, show) {
        $('.qsShowCover', li$).attr('data-original', RenderUtils.imagePathToURL(show.cover));
        $('.qsPortrait', li$).css('background-image', RenderUtils.imagePathToBackground(show.modelRef.portrait));
        $('.qsName', li$).text(show.modelRef.name);
        $('.qsRole', li$).text(show.modelRef.modelInfo.title);
        $('.qsHeight', li$).text(RenderUtils.heightToDisplay(show.modelRef.height));
        $('.qsWeight', li$).text(RenderUtils.weightToDisplay(show.modelRef.weight));
        $('.qsStatus', li$).text(show.modelRef.modelInfo.status);
        $('.qsNumFollowers', li$).text(show.modelRef.$numFollowerRefs);

        // User click here to avoid conflict with gesture
        $('.qsShowCover', li$).on('click', function() {
            appRuntime.view.to('app/views/show/S03Show', {
                'show' : show
            });
        }.bind(this));
        // User click here to avoid conflict with gesture
        $('.qsModel', li$).on('click', function() {
            appRuntime.view.to('app/views/producer/P02Model', {
                'model' : show.modelRef
            });
        }.bind(this));
        return li$;
    };

    return ShowGallery;
});
