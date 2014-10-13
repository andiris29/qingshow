// @formatter:off
define([
    'app/managers/TemplateManager',
    'app/components/header/HeaderBase'
], function(TemplateManager, HeaderBase) {
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

            // Render
            if (!data.left || data.left === 'back') {
                $('.qsLeft .qsText', this._dom$).addClass('fa fa-chevron-left');
                $('.qsLeft', this._dom$).on(appRuntime.events.click, function() {
                    appRuntime.view.back();
                }.bind(this));
            } else {
                $('.qsLeft .qsText', this._dom$).text(data.left);
                $('.qsLeft', this._dom$).on(appRuntime.events.click, function() {
                    this.trigger('clickLeft');
                }.bind(this));
            }
            if (data.right) {
                $('.qsRight .qsText', this._dom$).text(data.right);
                $('.qsRight', this._dom$).on(appRuntime.events.click, function() {
                    this.trigger('clickRight');
                }.bind(this));
            }

            this.title(data.title);
        }.bind(this));
    };

    andrea.oo.extend(CommonHeader, HeaderBase);

    return CommonHeader;
});
