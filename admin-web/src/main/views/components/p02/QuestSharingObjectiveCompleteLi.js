// @formatter:off
define([
    'main/services/codeMongoService'
], function(
    codeMongoService
) {
// @formatter:on
    var QuestSharingObjectiveCompleteLi = function(dom, initOptions) {
        QuestSharingObjectiveCompleteLi.superclass.constructor.apply(this, arguments);

        var text = '推送－分享任务达成';
        $('#anchor', this._dom).text(text).on('click', function() {
            alertify.confirm(text, function(e) {
                if (e) {
                    this._ownerView.request('/notify/questSharingObjectiveComplete', 'post', {}, function(err, metadata, data) {
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
    violet.oo.extend(QuestSharingObjectiveCompleteLi, violet.ui.UIBase);

    return QuestSharingObjectiveCompleteLi;
});
