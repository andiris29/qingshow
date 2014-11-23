// @formatter:off
define([
    'ui/UIComponent',
    'app/model',
    'app/managers/TemplateManager',
    'app/services/InteractionService',
    'app/services/QueryService',
    'app/utils/CodeUtils',
    'app/utils/RenderUtils'
], function(UIComponent, model, TemplateManager, InteractionService, QueryService, CodeUtils, RenderUtils) {
// @formatter:on
    /**
     *
     */
    var Collocated = function(dom, data) {
        Collocated.superclass.constructor.apply(this, arguments);

        this._pItemsInfo = {};

        this._coverTplt$ = null;
        this._coverParent$ = null;

        var loadTemplate = function(callback) {
            TemplateManager.load('producer/collocated.html', function(err, content$) {
                this._coverTplt$ = $('.clone', content$);
                this._coverParent$ = this._coverTplt$.parent();
                this._coverTplt$.remove();

                this._dom$.append(content$);

                callback();
            }.bind(this));
        }.bind(this);

        async.series([loadTemplate], function() {
            this._render();
        }.bind(this));

        this.reset();
    };
    andrea.oo.extend(Collocated, UIComponent);

    Collocated.prototype.getPreferredSize = function() {
        return {
            'height' : 100
        };
    };
    Collocated.prototype.reset = function() {
        if (!model.user()) {
            return;
        }
        this._refreshCollocated();
    };
    Collocated.prototype.collocate = function(pItem) {
        if (this._pItemsInfo[pItem._id]) {
            return;
        }
        var collocated$ = this._coverTplt$.clone().appendTo(this._coverParent$);
        collocated$.hide().fadeIn(300, function() {
        }.bind(this));

        $('.qsPItemCover', collocated$).css('background-image', RenderUtils.imagePathToBackground(pItem.cover));
        collocated$.on(appRuntime.events.click, function() {
            this.uncollocate(pItem);
            this.trigger('uncollocate', pItem);
        }.bind(this));

        this._pItemsInfo[pItem._id] = {
            'collocated$' : collocated$,
            'pItem' : pItem
        };

        this._validateDisplay();
    };

    Collocated.prototype.uncollocate = function(pItem) {
        if (!this._pItemsInfo[pItem._id]) {
            return;
        }
        var collocated$ = this._pItemsInfo[pItem._id].collocated$;
        collocated$.fadeOut(300, function() {
            collocated$.remove();
        });
        delete this._pItemsInfo[pItem._id];

        this._validateDisplay();
    };

    Collocated.prototype._render = function() {
        Collocated.superclass._render.apply(this, arguments);

        var confirmContainer$ = $('.qsConfirmContainer', this._dom$);
        var confirm$ = $('.qsConfirm', confirmContainer$);
        confirmContainer$.on(appRuntime.events.click, function() {
            if (confirm$.hasClass('qsDisabled')) {
                return;
            }
            var _ids = [];
            for (var _id in this._pItemsInfo) {
                _ids.push(_id);
            }
            var confirmed = confirm('确定搭配吗？');
            if (confirmed) {
                _.each(this._pItemsInfo, function(info) {
                    var pItem = info.pItem;
                    this.uncollocate(pItem);
                }.bind(this));
                InteractionService.collocate(_ids, function(metadata, data) {
                    if (metadata.error) {
                        alert(metadata.error);
                    } else {
                        this.trigger('save');
                        this._refreshCollocated();
                    }
                }.bind(this));
            }
        }.bind(this));
        this._validateDisplay();
    };

    Collocated.prototype._validateDisplay = function() {
        var confirm$ = $('.qsConfirm', this._dom$);

        if (_.size(this._pItemsInfo)) {
            confirm$.removeClass('qsDisabled');
        } else {
            confirm$.addClass('qsDisabled');
        }
    };
    Collocated.prototype._refreshCollocated = function(callback) {
        QueryService.pShowsByModel(model.user()._id, function(metadata, data) {
            $('.qsNumCollocated', this._dom$).text(data.pShows.length);

            if (callback) {
                callback();
            }
        });
    };

    return Collocated;
});
