// @formatter:off
define([
    'ui/containers/IScrollContainer',
    'app/model',
    'app/services/UserService',
    'app/views/ViewBase',
    'app/components/common/Header',
    'app/components/common/Navigator',
    'app/components/producer/Model',
    'app/components/producer/FanGallery',
    'app/components/show/ShowGallery'
], function(IScrollContainer, model, UserService, ViewBase, Header, Navigator, Model, FanGallery, ShowGallery) {
// @formatter:on
    /**
     *
     */
    var P05Collocation = function(dom, data) {
        P05Collocation.superclass.constructor.apply(this, arguments);

        this.loginRequired = true;

        var header = new Header($('<div/>').appendTo(this._dom$), {
            'left' : null,
            'right' : '登出'
        });
        header.on('clickRight', function() {
            UserService.logout(function() {
                model.user(null);
                appRuntime.view.to('app/views/user/U06Login');
            });
        });
    };
    andrea.oo.extend(P05Collocation, ViewBase);

    return P05Collocation;
});
