define([
    'main/services/codeMongoService'
],function (
    codeMongoService
){
    var BonusSummayLi = function (dom, initOptions) {
        BonusSummayLi.superclass.constructor.apply(this, arguments);

        $('#anchor', this._dom).on('click', function() {
            this._ownerView.push('main/views/P07BonusList', initOptions);
        }.bind(this));
    };

    violet.oo.extend(BonusSummayLi, violet.ui.UIBase);

    return BonusSummayLi;
});
