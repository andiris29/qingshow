// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/model'
], function(UIComponent, TemplateManager, model) {
// @formatter:on
    /**
     * The top level dom element, which will fit to screen
     */
    var ComparisonNavi = function(dom, data) {
        ComparisonNavi.superclass.constructor.apply(this, arguments);

        data = data || {};
        if (_.isString(data)) {
            data = {
                'title' : data
            };
        }

        TemplateManager.load('show/comparison-navi.html', function(err, content$) {
            this._dom$.append(content$);

            this._renderButton($('.qsLeft', this._dom$), data.left || ComparisonNavi.BUTTON_BACK);
            this._renderButton($('.qsRight', this._dom$), data.right || ComparisonNavi.BUTTON_USER);
            $('.qsLeft', this._dom$).on(appRuntime.events.click, function() {
                this.trigger('clickLeft');
            }.bind(this));
            $('.qsRight', this._dom$).on(appRuntime.events.click, function() {
                this.trigger('clickRight');
            }.bind(this));

            if (data.title) {
                $('.qsTitle', this._dom$).text(data.title).css('background-image', 'none');
            }
        }.bind(this));

        this._menu = null;
    };
    andrea.oo.extend(ComparisonNavi, UIComponent);

    ComparisonNavi.BUTTON_BACK = {
        'cssClass' : 'fa fa-chevron-left'
    };
    ComparisonNavi.BUTTON_MENU = {
        'cssClass' : 'fa fa-bars'
    };
    ComparisonNavi.BUTTON_USER = {
        'cssClass' : 'fa fa-user'
    };

    ComparisonNavi.prototype.getPreferredSize = function() {
        return {
            'height' : 96
        };
    };

    ComparisonNavi.prototype._renderButton = function(container$, button) {
        if (_.isString(button)) {
            $('.qsText', container$).text(button);
        } else {
            $('.qsText', container$).addClass(button.cssClass);
            if (button === ComparisonNavi.BUTTON_BACK) {
                container$.on(appRuntime.events.click, this._backHandler.bind(this));
            } else if (button === ComparisonNavi.BUTTON_MENU) {
                container$.on(appRuntime.events.click, this._menuHandler.bind(this));
            } else if (button === ComparisonNavi.BUTTON_USER) {
                container$.on(appRuntime.events.click, this._userHandler.bind(this));
            }
        }
    };

    ComparisonNavi.prototype._backHandler = function() {
        appRuntime.view.back();
    };

    ComparisonNavi.prototype._menuHandler = function() {
        if (this._menu) {
            return;
        }
        appRuntime.popup.create('app/components/common/Menu', {
            'closeClickOutside' : true
        }, function(popup) {
            this._menu = popup;
            this._menu.on('destroy', function() {
                this._menu = null;
            }.bind(this));
            appRuntime.popup.dock(this._menu, this._dom$, {
                'align' : 'lb',
                'direction' : 'down',
                'gap' : 0
            });
        }.bind(this));
    };

    ComparisonNavi.prototype._userHandler = function() {
        if (model.user()) {
            appRuntime.view.to('app/views/user/U01User');
        } else {
            appRuntime.view.to('app/views/user/U06Login');
        }
    };

    return ComparisonNavi;
});
