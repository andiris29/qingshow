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
    var CommonHeader = function(dom, data) {
        CommonHeader.superclass.constructor.apply(this, arguments);

        if (_.isString(data)) {
            data = {
                'title' : data
            };
        }

        TemplateManager.load('header/common-header.html', function(err, content$) {
            this._dom$.append(content$);

            this._renderButton($('.qsLeft', this._dom$), data.left || CommonHeader.BUTTON_BACK);
            this._renderButton($('.qsRight', this._dom$), data.right || CommonHeader.BUTTON_USER);
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
    andrea.oo.extend(CommonHeader, UIComponent);

    CommonHeader.BUTTON_BACK = {
        'cssClass' : 'fa fa-chevron-left'
    };
    CommonHeader.BUTTON_MENU = {
        'cssClass' : 'fa fa-bars'
    };
    CommonHeader.BUTTON_USER = {
        'cssClass' : 'fa fa-user'
    };

    CommonHeader.prototype.getPreferredSize = function() {
        return {
            'height' : 96
        };
    };

    CommonHeader.prototype._renderButton = function(container$, button) {
        if (_.isString(button)) {
            $('.qsText', container$).text(button);
        } else {
            $('.qsText', container$).addClass(button.cssClass);
            if (button === CommonHeader.BUTTON_BACK) {
                container$.on(appRuntime.events.click, this._backHandler.bind(this));
            } else if (button === CommonHeader.BUTTON_MENU) {
                container$.on(appRuntime.events.click, this._menuHandler.bind(this));
            } else if (button === CommonHeader.BUTTON_USER) {
                container$.on(appRuntime.events.click, this._userHandler.bind(this));
            }
        }
    };

    CommonHeader.prototype._backHandler = function() {
        appRuntime.view.back();
    };

    CommonHeader.prototype._menuHandler = function() {
        if (this._menu) {
            return;
        }
        appRuntime.popup.create('app/components/Menu', {
            'closeClickOutside' : true
        }, function(popup) {
            this._menu = popup;
            this._menu.on('destroy', function() {
                this._menu = null;
            }.bind(this));
            appRuntime.popup.dock(this._menu, this._dom$, 'lb', 'down', 0);
        }.bind(this));
    };

    CommonHeader.prototype._userHandler = function() {
        if (model.user()) {
            // TODO go U01User
            appRuntime.view.to('app/views/user/U02UserSetting');
        } else {
            appRuntime.view.to('app/views/user/U06Login');
        }
    };

    return CommonHeader;
});
