// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager'
], function(UIComponent, TemplateManager) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var BackableHeader = function(dom, data) {
        BackableHeader.superclass.constructor.apply(this, arguments);

        TemplateManager.load('header/backable-header.html', function(err, content$) {
            this._dom$.append(content$);

            // Render
            $('.qsTitle', this._dom$).text(data);

            $('.qsBack', this._dom$).on(appRuntime.events.click, function() {
                appRuntime.view.back();
            }.bind(this));
        }.bind(this));
    };

    andrea.oo.extend(BackableHeader, UIComponent);

    BackableHeader.prototype.getPreferredSize = function() {
        return {
            'height' : 96
        };
    };

    return BackableHeader;
});
