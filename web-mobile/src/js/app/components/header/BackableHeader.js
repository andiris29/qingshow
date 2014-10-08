// @formatter:off
define([
    'app/managers/TemplateManager',
    'app/components/header/HeaderBase'
], function(TemplateManager, HeaderBase) {
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
            // Event listener
            this._onBack();
        }.bind(this));
    };

    andrea.oo.extend(BackableHeader, HeaderBase);

    return BackableHeader;
});
