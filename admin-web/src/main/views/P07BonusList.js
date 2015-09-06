define([
	 'main/core/model',
    'main/views/View'
	],function (
    model,
    View
){
    violet.ui.factory.registerDependencies('main/views/P07BonusList', [
        'main/views/components/p07/BonusTr']);

    var P07BonusList = function (dom, initOptions) {
        P07BonusList.superclass.constructor.apply(this, arguments);
        this._refresh();
    };

    violet.oo.extend(P07BonusList, View);

    P07BonusList.prototype._refresh = function(){
        var parent$ = $('tbody', this._dom).empty();

        this.request('/userBonus/queryWithdrawRequested', 'get', {
        }, function (err, metadata, data) {
            data.peoples.forEach( function(people) {
                violet.ui.factory.createUi('main/views/components/p07/BonusTr', {
                    'people' : people
                }, parent$, this);
            }.bind(this));
        }.bind(this));
    };

    return P07BonusList;
});