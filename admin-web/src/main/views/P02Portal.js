// @formatter:off
define([
    'main/views/View'
], function(
    View
) {
// @formatter:on
    var P02Portal = function(dom, initOptions) {
        P02Portal.superclass.constructor.apply(this, arguments);

        this._initManagerTrade();
    };

    P02Portal.prototype._initManagerTrade = function() {
        var ul$ = $('#managerTrade', this._dom);
        var tasks = [1, 2, 7, 11, 16].map( function(status) {
            return function(callback) {
                violet.ui.factory.createUi('main/views/components/p02/TradeLi', {
                    'status' : status
                }, ul$, this, callback);
            }.bind(this);
        }.bind(this));
        async.series(tasks);
    };

    violet.oo.extend(P02Portal, View);

    return P02Portal;
});
