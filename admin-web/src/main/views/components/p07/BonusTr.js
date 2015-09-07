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

        var people = initOptions.people;

        var td$ = $('td', this._dom);

        td$.eq(0).text(people._id);
        td$.eq(1).text(people.nickname);

        td$.eq(2).text(function () {
            var totalBonus = 0;
            people.bonuses.forEach(function (val) {
                totalBonus += val.money;
            });
            return totalBonus;
        });

        td$.eq(3).text(getMoneyByStatus(0));
        td$.eq(4).text(getMoneyByStatus(1));
        td$.eq(5).text(getMoneyByStatus(2));

        td$.eq(6).on('click', this._submit.bind(this));

        function getMoneyByStatus(status) {
            var money = 0;
            people.bonuses.forEach(function (val) {
                if (val.status === status)
                    money += val.money;
            });
            return money;
        }

    };

    BonusTr.prototype._submit = function () {
        this.request('/userBonus/withdrawComplete', 'post', {}, function (err, metadata, data) {
            if (err) {
                alertify.error('提交失败');
            } else {
                alertify.success('提交成功');
            }
        })
    };

    violet.oo.extend(BonusTr, View);

    return BonusTr;
});
