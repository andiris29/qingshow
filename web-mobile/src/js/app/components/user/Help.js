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
     * Help's UI Componets
     */
    var Help = function(dom) {
        Help.superclass.constructor.apply(this, arguments);

        var type = arguments[1].type;

        async.parallel([ function(callback) {
            // load template
            TemplateManager.load('user/help-regulations.html', true, function(err, content$) {
                this._dom$.append(content$);
                callback(null);
            }.bind(this));
        }.bind(this)], function(err, results) {
            if (type == "help") {
                this._renderHelp();
            } else {
                this._renderRegulations();
            }
        }.bind(this));
    };

    andrea.oo.extend(Help, UIComponent);

    Help.prototype._renderHelp = function() {
        var view$ = $('.qsTpltHelpMain', this._dom$);
        $('.qsSubTitle', view$).text("帮助");
        $('.qsRow', view$).html("1.XXXXXXXXXXXXXXXXXX <br> 2.OOOOOOOOOOOOOOOO<br> 3.XXXXXXXXXXXXXXXXXXX");
    };
    Help.prototype._renderRegulations = function() {
        var view$ = $('.qsTpltHelpMain', this._dom$);
        $('.qsSubTitle', view$).text("使用条例");
        $('.qsRow', view$).html("1.XXXXXXXXXXXXXXXXXX <br> 2.OOOOOOOOOOOOOOOO<br> 3.XXXXXXXXXXXXXXXXXXX");
    };

    return Help;
});
