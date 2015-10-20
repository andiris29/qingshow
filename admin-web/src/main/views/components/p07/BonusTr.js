// @formatter:off
define([
    'main/services/codeMongoService',
    'main/views/View'
], function (codeMongoService,
             View
) {
// @formatter:on
    var BonusTr = function (dom, initOptions) {
        BonusTr.superclass.constructor.apply(this, arguments);

        var people = this._people = initOptions.people;
        var td$ = $('td', this._dom);

        td$.eq(0).text(people._id);
        td$.eq(1).text(people.id);
        td$.eq(2).text(people.nickname);
        td$.eq(3).text(people.count);
        td$.eq(4).text(people.sum);
        td$.eq(5).text(people.alipayId);
        td$.eq(6).on('click', this._submit.bind(this));

    };

    BonusTr.prototype._submit = function () {
        var people = this._people;
        this.request('/userBonus/withdrawComplete', 'post', {
            '_id' : people._id,
            'count' : people.count,
            'sum' : people.sum
        }, function (err, metadata, data) {
            if (err) {
                alertify.error('提交失败');
            } else {
                alertify.success('提交成功');
            }
        });
    };

    violet.oo.extend(BonusTr, View);

    return BonusTr;
});
