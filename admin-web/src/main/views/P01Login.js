// @formatter:off
define([
    'main/views/View'
], function(
    View
) {
// @formatter:on
    var P01Login = function(dom, initOptions) {
        P01Login.superclass.constructor.apply(this, arguments);

        var submit$ = $('button', this._dom);
        submit$.on('click', this._login.bind(this));

        // Auto login
        this.request('/user/get', 'get', {}, function(err, metadata, data) {
            if (err || metadata.error) {
            } else {
                alertify.success('自动登录成功');
                this.push('main/views/P02Portal');
            }
        }.bind(this));
    };
    violet.oo.extend(P01Login, View);

    P01Login.prototype._login = function() {
        var idOrNickName$ = $('#exampleInputEmail1', this._dom);
        var password$ = $('#exampleInputPassword1', this._dom);

        var idOrNickName = idOrNickName$.val();
        var password = password$.val();
        if (!idOrNickName) {
            alertify.error('需要输入用户名');
            return;
        }
        if (!password) {
            alertify.error('需要输入密码');
            return;
        }
        this.request('/user/login', 'post', {
            'id' : idOrNickName,
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
