// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/services/DataService',
    'app/utils/CodeUtils',
    'app/utils/RenderUtils'
], function(UIComponent, TemplateManager, DataService, CodeUtils, RenderUtils) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var User = function(dom, data) {
        User.superclass.constructor.apply(this, arguments);
        this._user = data;

        TemplateManager.load('user/user.html', function(err, content$) {
            this._dom$.append(content$);
            this._render();
        }.bind(this));
    };
    andrea.oo.extend(User, UIComponent);

    User.prototype.getPreferredSize = function() {
        return {
            'height' : 320
        };
    };

    User.prototype.numShowsLike = function(value) {
        $('.qsNumShowsLike', this._dom$).text(value);
    };
    User.prototype.numShowsRecommendation = function(value) {
        $('.qsNumShowsRecommendation', this._dom$).text(value);
    };
    User.prototype.numShowsFollow = function(value) {
        $('.qsNumShowsFollow', this._dom$).text(value);
    };

    User.prototype._render = function() {
        User.superclass._render.apply(this, arguments);

        $('.qsPortrait', this._dom$).css('background-image', RenderUtils.imagePathToBackground(this._user.portrait));
        $('.qsName', this._dom$).text(this._user.name);
        // TODO
        $('.qsAge', this._dom$).text('');
        $('.qsDetail', this._dom$).text([RenderUtils.heightToDisplay(this._user.height), RenderUtils.weightToDisplay(this._user.weight)].join('／'));

        var tabs$ = $('.qsTab', this._dom$);
        tabs$.on(appRuntime.events.click, function(event) {
            var tab$ = $(event.currentTarget);
            if (tab$.hasClass('qsTabSelected')) {
                return;
            }

            $('.qsTab', this._dom$).removeClass('qsTabSelected');
            tab$.addClass('qsTabSelected');
            this.trigger('selectTab', tabs$.index(tab$));
        }.bind(this));
    };

    return User;
});
