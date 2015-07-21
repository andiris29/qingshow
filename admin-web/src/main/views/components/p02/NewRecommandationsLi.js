// @formatter:off
define([
    'main/services/codeMongoService'
], function(
    codeMongoService
) {
// @formatter:on
    var NewRecommandationsLi = function(dom, initOptions) {
        NewRecommandationsLi.superclass.constructor.apply(this, arguments);

        var group = initOptions.group;
        var text = violet.string.substitute('推送－{0}新搭配', codeMongoService.toNameWithCode('show.recommend.group', group));
        $('#anchor', this._dom).text(text).on('click', function() {
            alertify.confirm(text, function(e) {
                if (e) {
                    this._ownerView.request('/notify/newRecommandations', 'post', {
                        'group' : group
                    }, function(err, metadata, data) {
                        if (err || metadata.error) {
                            alertify.success('推送失败');
                        } else {
                            alertify.success('推送成功');
                        }
                    }.bind(this));
                }
            }.bind(this));
        }.bind(this));
    };
    violet.oo.extend(NewRecommandationsLi, violet.ui.UIBase);

    return NewRecommandationsLi;
});
