// @formatter:off
define([
    'app/managers/TemplateManager',
    'app/components/header/HeaderBase'
], function(TemplateManager, HeaderBase) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var SaveableHeader = function(dom, data) {
        SaveableHeader.superclass.constructor.apply(this, arguments);

        TemplateManager.load('header/saveable-header.html', function(err, content$) {
            this._dom$.append(content$);

            // Render
            this.title(data);
            // Event listener
            this._onBack();
        }.bind(this));
    };

    andrea.oo.extend(SaveableHeader, HeaderBase);

    return SaveableHeader;
});

