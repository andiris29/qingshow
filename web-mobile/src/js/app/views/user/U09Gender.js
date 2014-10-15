// @formatter:off
define([
    'ui/scroll/IScrollContainer',
    'app/views/ViewBase',
    'app/components/header/CommonHeader',
    'app/components/user/HairTypeComponent',
    'app/model'
], function(IScrollContainer, ViewBase, CommonHeader, HairTypeComponent, model) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var U09Gender = function(dom) {
        U09Gender.superclass.constructor.apply(this, arguments);

        var header = new CommonHeader($('<div/>').appendTo(this._dom$), {
            'title' : '设置',
            'right' : '保存'
        });

        header.on('clickRight', function(event) {
            // TODO
            // DataService.request('/user/update', main.save(), function() {
                // appRuntime.view.back();
            // });
        });

        var body = new IScrollContainer($('<div/>').css({
            'width' : '100%',
            'height' : this._dom$.height() - header.getPreferredSize().height
        }).appendTo(this._dom$));

        var main = new HairTypeComponent($('<div/>'), model);
        body.append(main);
    };
    andrea.oo.extend(U09Gender, ViewBase);

    return U09Gender;
});


