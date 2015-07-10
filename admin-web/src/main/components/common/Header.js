// @formatter:off
define([
    'main/core/UI'
], function(
    UI
) {
// @formatter:on
    var Header = function(dom, initOptions) {
        Header.superclass.constructor.apply(this, arguments);
    };
    violet.oo.extend(Header, UI);

    return Header;
});
