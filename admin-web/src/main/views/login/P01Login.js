// @formatter:off
define([
    'main/views/View'
], function(
    View
) {
// @formatter:on
    var P01Login = function(dom, initOptions) {
        P01Login.superclass.constructor.apply(this, arguments);

        var idOrNickName$ = $('#exampleInputEmail1', this._dom);
        var password$ = $('#exampleInputPassword1', this._dom);
        var submit$ = $('button', this._dom);

        submit$.on('click', function() {
            var idOrNickName = idOrNickName$.val();
            var password = password$.val();
            if (!idOrNickName) {
                return;
            }
            if (!password) {
                return;
            }
            this.request('/user/login', 'post', {
                'idOrNickName' : idOrNickName,
                'password' : password
            }, function(err, metadata, data) {
                if (err || metadata.error) {
                    alertify.error('Login fail.');
                }
            }.bind(this));
        }.bind(this));
    };
    violet.oo.extend(P01Login, View);

    return P01Login;
});
