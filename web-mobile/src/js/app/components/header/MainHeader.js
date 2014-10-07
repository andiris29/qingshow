// @formatter:off
define([
    'app/managers/TemplateManager',
    'app/components/header/HeaderBase'
], function(TemplateManager, HeaderBase) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var MainHeader = function(dom) {
        MainHeader.superclass.constructor.apply(this, arguments);

        TemplateManager.load('header/main-header.html', function(err, content$) {
            this._dom$.append(content$);
            this._onMenu()._onUser();
        }.bind(this));
    };

    andrea.oo.extend(MainHeader, HeaderBase);

    return MainHeader;
});
