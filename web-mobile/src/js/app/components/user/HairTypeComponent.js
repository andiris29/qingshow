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

        console.log(this._data);
        async.parallel([function(callback) {
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
        var hairType = CodeUtils.getCodes('people.hairType');

        var view$ = $('.qsTpltHairTypeMain', this._dom$);
        var contain$ = $('.qsListInfoSection', view$);
        var tplt$ = $('.qsHidden', view$);

        hairType.forEach(function(hair, index) {
            var li$ = $(".qsRow", tplt$).clone();
            $('.qsTitle', li$).text(hair);
            $('div:last', li$).attr('id', 'hairType-'+index);
            li$.bind('click', function() {
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
    };

    return HairTypeComponent;
});
