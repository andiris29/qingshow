// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/services/InteractionService',
    'app/utils/CodeUtils',
    'app/utils/RenderUtils'
], function(UIComponent, TemplateManager, InteractionService, CodeUtils, RenderUtils) {
// @formatter:on
    /**
     *
     */
    var Collocated = function(dom, data) {
        Collocated.superclass.constructor.apply(this, arguments);

        this._pItems = {};

        this._coverTplt$ = null;
        this._coverParent$ = null;
        TemplateManager.load('producer/collocated.html', function(err, content$) {
            this._coverTplt$ = $('.clone', content$);
            this._coverParent$ = this._coverTplt$.parent();
            this._coverTplt$.remove();

            this._dom$.append(content$);

            this._render();
        }.bind(this));
    };
    andrea.oo.extend(Collocated, UIComponent);

    Collocated.prototype.getPreferredSize = function() {
        return {
            'height' : 100
        };
    };

    Collocated.prototype.collocate = function(pItem) {
        if (this._pItems[pItem._id]) {
            return;
        }
        var cover$ = this._coverTplt$.clone().appendTo(this._coverParent$);
        cover$.css('background-image', RenderUtils.imagePathToBackground(pItem.cover));
        cover$.on('click', function() {
            this.uncollocate(pItem);
            this.trigger('uncollocate', pItem);
        }.bind(this));

        this._pItems[pItem._id] = {
            'cover$' : cover$,
            'pItem' : pItem
        };
        this._validateDisplay();
    };

    Collocated.prototype.uncollocate = function(pItem) {
        if (!this._pItems[pItem._id]) {
            return;
        }
        this._pItems[pItem._id].cover$.remove();
        delete this._pItems[pItem._id];

        this._validateDisplay();
    };

    Collocated.prototype._render = function() {
        Collocated.superclass._render.apply(this, arguments);

        var confirm$ = $('.qsConfirm', this._dom$);
        confirm$.on('click', function() {
            if (confirm$.hasClass('qsDisabled')) {
                return;
            }
            var _ids = [];
            for (var _id in this._pItems) {
                _ids.push(_id);
            }
            InteractionService.collocate(_ids, function() {
                // TODO
            });
        });
        this._validateDisplay();
    };

    Collocated.prototype._validateDisplay = function() {
        var confirm$ = $('.qsConfirm', this._dom$);
        if (this._coverParent$.children().length === 0) {
            confirm$.addClass('qsDisabled');
        } else {
            confirm$.removeClass('qsDisabled');
        }
    };

    return Collocated;
});
