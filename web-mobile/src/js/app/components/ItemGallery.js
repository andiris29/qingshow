// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/services/DataService',
    'app/utils/DateUtils'
], function(UIComponent, TemplateManager, DataService, DateUtils) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var Item, People;
    var ItemGallery = function(dom) {
        ItemGallery.superclass.constructor.apply(this, arguments);
        require(['app/views/Item', 'app/views/People'], function(rItem, rPeople) {
            Item = rItem;
            People = rPeople;
        });

        this._tpltLiPeople$ = null;

        this._dom$.css('overflow', 'auto');
        async.parallel([ function(callback) {
            // Load template
            TemplateManager.load('item-gallery.html', function(err, content$) {
                content$.css('minHeight', this._dom$.height() + 'px');
                this._dom$.append(content$);

                callback(null);
            }.bind(this));
        }.bind(this), function(callback) {
            // Load template for list items
            TemplateManager.load('item-gallery-li.html', function(err, content$) {
                callback(null, content$);
            }.bind(this));
        }.bind(this), function(callback) {
            // Load data
            DataService.request('/peoples', null, callback);
        }.bind(this)], function(err, results) {
            this._tpltLiPeople$ = results[1];
            this._render(results[2]);
        }.bind(this));
    };

    andrea.oo.extend(ItemGallery, UIComponent);

    ItemGallery.prototype._render = function(peoples) {
        var containers$ = $('.qsLiItemContainer', this._dom$);

        peoples.forEach( function(people, index) {
            var liPeople$ = this._renderPeople(this._tpltLiPeople$.clone(), people);
            var targetContainer$;
            containers$.each(function(index, container) {
                if (!targetContainer$ || targetContainer$.children().length > $(container).children().length) {
                    targetContainer$ = $(container);
                }
            });
            liPeople$.appendTo(targetContainer$);
        }.bind(this));

        $('img', this._dom).on('load', function() {
            this.trigger('resize');
        }.bind(this));
    };

    ItemGallery.prototype._renderPeople = function(liPeople$, people) {
        $('.qsItemCover', liPeople$).attr('src', people.cover);
        $('.qsItemDescription', liPeople$).text(people.itemDescription);
        $('.qsPortrait', liPeople$).css('background-image', andrea.string.substitute('url("{0}")', people.portrait));
        $('.qsName', liPeople$).text(people.name);
        $('.qsAge', liPeople$).text(DateUtils.parseAge(people.birthtime) + '岁');
        $('.qsStatus', liPeople$).text(people.status);
        $('.qsNumLikes', liPeople$).text(people.numLikes);

        $('.qsItemCover, .qsItemDescription', liPeople$).on('click', function() {
            appRuntime.view.to(Item);
        }.bind(this));
        $('.qsPeople', liPeople$).on('click', function() {
            appRuntime.view.to(People);
        }.bind(this));
        return liPeople$;
    };

    return ItemGallery;
});
