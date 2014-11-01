// @formatter:off
define([
    'ui/containers/IScrollContainer',
    'app/views/ViewBase',
    'app/services/UserService',
    'app/components/common/Header',
    'app/components/user/Help',
    'app/model'
], function(IScrollContainer, ViewBase, UserService, Header, Help, model) {
// @formatter:on
    
    var U03Help = function(dom) {
        U03Help.superclass.constructor.apply(this, arguments);

        var type = arguments[1].type;
        var title = "使用条例";
        if (type == "help") {
            title = "帮助";
        }

        var header = new Header($('<div/>').appendTo(this._dom$), title);

        var body = new IScrollContainer($('<div/>').css({
            'width' : '100%',
            'height' : this._dom$.height() - header.getPreferredSize().height
        }).appendTo(this._dom$));

        var main = new Help($('<div/>'), {"type": type});
        body.append(main);
    };

    andrea.oo.extend(U03Help, ViewBase);

    return U03Help;

});
