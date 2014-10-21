// @formatter:off
define([
    'ui/containers/IScrollContainer',
    'app/views/ViewBase',
    'app/components/common/Header',
    'app/services/UserService',
    'app/components/user/RegisterComponent',
    'app/model'
], function(IScrollContainer, ViewBase, Header, UserService, RegisterComponent, model) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var U07Register = function(dom) {
        U07Register.superclass.constructor.apply(this, arguments);

        var header = new Header($('<div/>').appendTo(this._dom$), '注册');

        var body = new IScrollContainer($('<div/>').css({
            'width' : '100%',
            'height' : this._dom$.height() - header.getPreferredSize().height
        }).appendTo(this._dom$));

        var main = new RegisterComponent($('<div/>'), model);
        body.append(main);

    };
    andrea.oo.extend(U07Register, ViewBase);

    return U07Register;
});

