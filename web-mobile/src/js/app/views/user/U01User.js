// @formatter:off
define([
    'ui/containers/IScrollContainer',
    'app/services/FeedingService',
    'app/views/ViewBase',
    'app/components/common/Header',
    'app/components/common/Navigator',
    'app/components/user/User',
    'app/components/show/ShowGallery'
], function(IScrollContainer, FeedingService, ViewBase, Header, Navigator, User, ShowGallery) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var U01User = function(dom) {
        U01User.superclass.constructor.apply(this, arguments);

        var header = new Header($('<div/>').appendTo(this._dom$), {
            'right' : '设置'
        });
        header.on('clickRight', function() {
            appRuntime.view.to('app/views/user/U02UserSetting');
        });
        var body = new IScrollContainer($('<div/>').css({
            'width' : '100%',
            'height' : this._dom$.height() - header.getPreferredSize().height
        }).appendTo(this._dom$));

        var user = new User($('<div/>'), this._model);
        body.append(user);

        var navi = new Navigator($('<div/>'));
        navi.append(new ShowGallery($('<div/>'), {
            'feeding' : FeedingService.choosen
        }));
        navi.append(new ShowGallery($('<div/>'), {
            'feeding' : FeedingService.choosen
        }));
        navi.append(new ShowGallery($('<div/>'), {
            'feeding' : FeedingService.choosen
        }));
        body.append(navi);
        //
        user.on('selectTab', function(event, index) {
            navi.index(index);
        });
    };
    andrea.oo.extend(U01User, ViewBase);

    return U01User;
});
