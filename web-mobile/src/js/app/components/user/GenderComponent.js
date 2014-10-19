// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/services/DataService',
    'app/utils/CodeUtils',
    'app/utils/RenderUtils'
], function(UIComponent, TemplateManager, DataService, CodeUtils, RenderUtils) {
// @formatter:on

    var GenderComponent = function(dom) {
        GenderComponent.superclass.constructor.apply(this, arguments);

        async.parallel([ function(callback) {
            // load template
            TemplateManager.load('user/gender.html', true, function(err, content$) {
                this._dom$.append(content$);
                callback(null);
            }.bind(this));
        }.bind(this)], function(callback) {
            this._render();
        }.bind(this));

    };

    andrea.oo.extend(GenderComponent, UIComponent);

    GenderComponent.prototype._render = function() {
        var view$ = $('.qsTpltGenderMain', this._dom$);
        var contain$ = $('.qsListInfoSection', view$);
        var tplt$ = $('.qsHidden', view$);

        [0, 1].forEach(function(code, index) {
            var li$ = $(".qsRow", tplt$).clone();
            $('.qsTitle', li$).text(CodeUtils.getValue('people.gender', code));
            $('div:last', li$).attr('id', 'gender-' + code);
            li$.bind(appRuntime.events.click, function() {
                if ($('.qsDisable', this).length > 0) {
                    var other$ =  $('.qsHighlight', this.parentElement);
                    other$.removeClass('fa-check-circle2 qsHighlight');
                    other$.toggleClass('fa-check-circle-o2 qsDisable');
                    $('div:last', this).removeClass('fa-check-circle-o2 qsDisable');
                    $('div:last', this).toggleClass('fa-check-circle2 qsHighlight');
                    
                } else {
                    var other$ =  $('.qsDisable', this.parentElement);
                    other$.removeClass('fa-check-circle-o2 qsDisable');
                    other$.toggleClass('fa-check-circle2 qsHighlight');

                    $('div:last', this).removeClass('fa-check-circle2 qsHighlight');
                    $('div:last', this).toggleClass('fa-check-circle-o2 qsDisable');
                }
            });
            li$.appendTo(contain$);
        });

        var element = this._data._user.gender;

        $('#gender-' + element, contain$).removeClass('fa-check-circle-o2 qsDisable');
        $('#gender-' + element, contain$).toggleClass('fa-check-circle2 qsHighlight');
    };

    GenderComponent.prototype.save = function() {
        var arry = [];
        $('.qsHighlight', this._dom$).each(function(index, element) {
            var id = element.id;
            arry[index] = id.replace('gender-', '');
        });
        return { "gender": arry};
    };

    return GenderComponent;
});

