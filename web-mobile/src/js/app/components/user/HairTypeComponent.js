// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/services/DataService',
    'app/utils/CodeUtils',
    'app/utils/RenderUtils'
], function(UIComponent, TemplateManager, DataService, CodeUtils, RenderUtils) {
// @formatter:on

    var HairTypeComponent = function(dom) {
        HairTypeComponent.superclass.constructor.apply(this, arguments);

        async.parallel([ function(callback) {
            // load template
            TemplateManager.load('user/hair-type.html', true, function(err, content$) {
                this._dom$.append(content$);
                callback(null);
            }.bind(this));
        }.bind(this)], function(callback) {
            this._render();
        }.bind(this));

    };

    andrea.oo.extend(HairTypeComponent, UIComponent);

    HairTypeComponent.prototype._render = function() {
        var view$ = $('.qsTpltHairTypeMain', this._dom$);
        var contain$ = $('.qsListInfoSection', view$);
        var tplt$ = $('.qsHidden', view$);
        ['all', 'midlong', 'long', 'superlong'].forEach(function(code, index) {
            var li$ = $(".qsRow", tplt$).clone();
            $('.qsTitle', li$).text(CodeUtils.getValue('people.hairType', code));
            $('div:last', li$).attr('id', 'hairType-' + code);
            li$.bind(appRuntime.events.click, function() {
                if ($('.qsDisable', this).length > 0) {
                    $('div:last', this).removeClass('fa-check-circle-o2 qsDisable');
                    $('div:last', this).toggleClass('fa-check-circle2 qsHighlight');
                } else {
                    $('div:last', this).removeClass('fa-check-circle2 qsHighlight');
                    $('div:last', this).toggleClass('fa-check-circle-o2 qsDisable');
                }
            });
            li$.appendTo(contain$);
        });

        this._data._user.hairTypes.forEach(function(element) {
            $('#hairType-' + element, contain$).removeClass('fa-check-circle-o2 qsDisable');
            $('#hairType-' + element, contain$).toggleClass('fa-check-circle2 qsHighlight');
        });
    };

    HairTypeComponent.prototype.save = function() {
        var arry = [];
        var selected = $('.qsHighlight', this._dom$);
        selected.each(function(index, element) {
            var id = element.id;
            arry[index] = id.replace('hairType-', '');
        });
        return { "hairType": arry};
    };

    return HairTypeComponent;
});
