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
    var Header = function(dom, data) {
        Header.superclass.constructor.apply(this, arguments);

        data = data || {};
        if (_.isString(data)) {
            data = {
                'title' : data
            };
        }

        TemplateManager.load('common/header.html', function(err, content$) {
            this._dom$.append(content$);

            this._renderButton($('.qsLeft', this._dom$), data.left || Header.BUTTON_BACK);
            this._renderButton($('.qsRight', this._dom$), data.right || Header.BUTTON_USER);
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
    andrea.oo.extend(Header, UIComponent);

    Header.BUTTON_BACK = {
        'cssClass' : 'fa fa-chevron-left'
    };
    Header.BUTTON_MENU = {
        'cssClass' : 'fa fa-bars'
    };
    Header.BUTTON_USER = {
        'cssClass' : 'fa fa-user'
    };

    Header.prototype.getPreferredSize = function() {
        return {
            'height' : 96
        };
    };

    Header.prototype._renderButton = function(container$, button) {
        if (_.isString(button)) {
            $('.qsText', container$).text(button);
        } else {
            $('.qsText', container$).addClass(button.cssClass);
            if (button === Header.BUTTON_BACK) {
                container$.on(appRuntime.events.click, this._backHandler.bind(this));
            } else if (button === Header.BUTTON_MENU) {
                container$.on(appRuntime.events.click, this._menuHandler.bind(this));
            } else if (button === Header.BUTTON_USER) {
                container$.on(appRuntime.events.click, this._userHandler.bind(this));
            }
        }
    };

    Header.prototype._backHandler = function() {
        appRuntime.view.back();
    };

    Header.prototype._menuHandler = function() {
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
            appRuntime.popup.dock(this._menu, this._dom$, 'lb', 'down', 0);
        }.bind(this));
    };

    Header.prototype._userHandler = function() {
        if (model.user()) {
            // TODO go U01User
            appRuntime.view.to('app/views/user/U02UserSetting');
        } else {
            appRuntime.view.to('app/views/user/U06Login');
        }
    };

    return Header;
});
