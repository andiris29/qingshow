// @formatter:off
define([
], function(
) {
// @formatter:on
    var Header = function(dom, initOptions) {
        Header.superclass.constructor.apply(this, arguments);

        var back$ = $('#back', this._dom);
        var logout$ = $('#logout', this._dom);

        initOptions = initOptions || {};
        if (initOptions.back === false) {
            back$.hide();
        } else {
            back$.on('click', function() {
                this._ownerView.pop();
            }.bind(this));
        }
        if (initOptions.logout === false) {
            logout$.hide();
        } else {
            logout$.on('click', function() {
                this._ownerView.request('/user/logout', 'post', {}, function(err, metadata, data) {
                    this._ownerView.popAll();
                }.bind(this));
            }.bind(this));
        }
    };
    violet.oo.extend(Header, violet.ui.UIBase);

    return Header;
});
