define([
    'main/services/codeMongoService'
],function (
    codeMongoService
){
    var BonusForgeLi = function (dom, initOptions) {
        BonusForgeLi.superclass.constructor.apply(this, arguments);

        $('#anchor', this._dom).on('click', function() {
            this._ownerView.push('main/views/P06BonusForge', initOptions);
        }.bind(this));
    };

    violet.oo.extend(BonusForgeLi, violet.ui.UIBase);

    return BonusForgeLi;
});

