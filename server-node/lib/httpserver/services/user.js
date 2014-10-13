var People = require('../../model/peoples');

var _login;
_login = function (req, res) {
    var param, mail, encryptedPassword;
    param = req.body;
    mail = param.mail || '';
    encryptedPassword = param.encryptedPassword || '';
    People.findOne({"userInfo.mail" : mail, "userInfo.encryptedPassword": encryptedPassword}, function (err, people) {
        if (err) {
            //TODO handle error
            res.send('err');
        }
        if (people) {
            //login succeed
            req.session.userId = people._id;
            req.session.loginDate = new Date();
            res.send(people);
        } else {
            //TODO handle login fail
            //login fail
            delete req.session.userId;
            delete req.session.loginData;
            res.send('login fail');
        }
    });
};

module.exports = {
    'login' : ['post', _login]
};