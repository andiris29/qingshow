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

        // Auto login
        this.request('/user/get', 'get', {}, function(err, metadata, data) {
            if (err || metadata.error) {
                submit$.on('click', this._login.bind(this));
            } else {
                this.push('main/views/P02Portal');
            }
        }.bind(this));
    };
    violet.oo.extend(P01Login, View);

    P01Login.prototype._login = function() {
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
                alertify.error('登录失败');
            } else {
                alertify.success('登录成功');
                this.push('main/views/P02Portal');
            }
        }.bind(this));
    };

    return P01Login;
});
