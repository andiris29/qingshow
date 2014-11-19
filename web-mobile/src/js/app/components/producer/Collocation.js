// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/services/DataService',
    'app/utils/CodeUtils',
    'app/utils/RenderUtils'
], function(UIComponent, TemplateManager, DataService, CodeUtils, RenderUtils) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var Collocation = function(dom, data) {
        Collocation.superclass.constructor.apply(this, arguments);

        TemplateManager.load('producer/collocation.html', function(err, content$) {
            this._dom$.append(content$);
            this._render();
        }.bind(this));
    };
    andrea.oo.extend(Collocation, UIComponent);

    Collocation.prototype.getPreferredSize = function() {
        return {
            'height' : 320
        };
    };

    Collocation.prototype.numUppers = function(value) {
        $('.numUppers', this._dom$).text(value);
    };
    Collocation.prototype.numLowers = function(value) {
        $('.numLowers', this._dom$).text(value);
    };
    Collocation.prototype.numShoes = function(value) {
        $('.numShoes', this._dom$).text(value);
    };
    Collocation.prototype.numAccessories = function(value) {
        $('.numAccessories', this._dom$).text(value);
    };

    Collocation.prototype._render = function() {
        Collocation.superclass._render.apply(this, arguments);

        var tabs$ = $('.qsTab', this._dom$);
        tabs$.on(appRuntime.events.click, function(event) {
            var tab$ = $(event.currentTarget);
            if (tab$.hasClass('qsTabSelected')) {
                return;
            }

            $('.qsTab', this._dom$).removeClass('qsTabSelected');
            tab$.addClass('qsTabSelected');
            this.trigger('selectTab', tabs$.index(tab$));
        }.bind(this));
    };

    return Collocation;
});
