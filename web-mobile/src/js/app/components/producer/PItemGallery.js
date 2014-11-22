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
     *
     */
    var PItemGallery = function(dom, data) {
        PItemGallery.superclass.constructor.apply(this, arguments);

        this._query = data.query;
        this._pageNo = 1;
        this._numTotal = 0;

        this._expandable = true;

        this._tpltLi$ = null;

        async.parallel([ function(callback) {
            // Load template
            TemplateManager.load('producer/pitem-gallery.html', function(err, content$) {
                content$.css('minHeight', this._dom$.height() + 'px');
                this._dom$.append(content$);

                callback(null);
            }.bind(this));
        }.bind(this), function(callback) {
            // Load template for list items
            TemplateManager.load('producer/pitem-gallery-li.html', function(err, content$) {
                callback(null, content$);
            }.bind(this));
        }.bind(this)], function(err, results) {
            this._tpltLi$ = results[1];
            // Load data
            this._query(this._pageNo, this._render.bind(this));
        }.bind(this));
    };

    andrea.oo.extend(PItemGallery, UIComponent);

    PItemGallery.prototype.expand = function() {
        if (!this._expandable) {
            return;
        }
        $('.qsLoading .qsSpin', this._dom$).fadeIn();
        $('.qsLoading .qsText', this._dom$).text('努力加载中…');

        this._pageNo++;
        this._query(this._pageNo, this._render.bind(this));
    };

    PItemGallery.prototype.reset = function() {
        var containers$ = $('.qsLiItemContainer', this._dom$);
        containers$.empty();
        this._pageNo = 1;
        this._query(this._pageNo, this._render.bind(this));
    };

    PItemGallery.prototype.uncollocate = function(pItem) {
        $('.qsCollocate', this._dom$).each(function(index, dom) {
            var collocate$ = $(dom);
            if (collocate$.data('pItem') && collocate$.data('pItem')._id === pItem._id) {
                collocate$.hide();
            }
        });
    };

    PItemGallery.prototype.numTotal = function() {
        return this._numTotal;
    };

    PItemGallery.prototype._render = function(metadata, data) {
        PItemGallery.superclass._render.apply(this, arguments);

        $('.qsLoading .qsSpin', this._dom$).hide();
        if (metadata.error) {
            this._expandable = false;

            if (this._pageNo === 1) {
                $('.qsRefreshInfo', this._dom$).hide();
            }
        } else {
            this._numTotal = metadata.numTotal;
            this._expandable = (FeedingService.PAGE_SIZE * this._pageNo) < this._numTotal;

            // Shows
            var containers$ = $('.qsLiItemContainer', this._dom$);
            data.pItems.forEach( function(pItem, index) {
                var li$ = this._renderOne(this._tpltLi$.clone(), pItem);
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

    PItemGallery.prototype._renderOne = function(li$, pItem) {
        $('.qsPItemCover', li$).attr('data-original', RenderUtils.imagePathToURL(pItem.cover));

        // User click here to avoid conflict with gesture
        $('.qsPItemCover', li$).on('click', function() {
            var collocate$ = $('.qsCollocate', li$).data('pItem', pItem);
            if (collocate$.is(":visible")) {
                collocate$.hide();
                this.trigger('uncollocate', pItem);
            } else {
                collocate$.show();
                this.trigger('collocate', pItem);
            }
        }.bind(this));
        return li$;
    };

    return PItemGallery;
});
