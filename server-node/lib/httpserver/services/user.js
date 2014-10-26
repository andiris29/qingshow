var People = require('../../model/peoples');
var ServicesUtil = require('../servicesUtil');
var ServerError = require('../server-error');

var _login, _logout, _update, _register;
_login = function (req, res) {
    var param, mail, encryptedPassword;
    param = req.body;
    mail = param.mail || '';
    encryptedPassword = param.encryptedPassword || '';
    People.findOne({"userInfo.mail" : mail, "userInfo.encryptedPassword": encryptedPassword}, function (err, people) {
        if (err) {
            ServicesUtil.responseError(res, err);
        } else if (people) {
            //login succeed
            req.session.userId = people._id;
            req.session.loginDate = new Date();

            var retData = {
                metadata: {
                    //TODO change invilidateTime
                    "invalidateTime": 3600000
                },
                data: {
                    people : people
                }
            };
            res.json(retData);
        } else {
            //login fail
            delete req.session.userId;
            delete req.session.loginDate;
            err = new ServerError(ServerError.IncorrectMailOrPassword);
            ServicesUtil.responseError(res, err);
        }
    });
};

_logout = function (req, res) {
    delete req.session.userId;
    delete req.session.loginDate;
    var retData = {
        metadata: {
            "result": 0
        }
    };
    res.json(retData);

}

_register = function (req, res) {
    var param, mail, encryptedPassword;
    param = req.body;
    mail = param.mail ;
    encryptedPassword = param.encryptedPassword;
    //TODO validate mail and encryptedPassword
    if (!mail || !encryptedPassword || !mail.length || !encryptedPassword.length) {
        ServicesUtil.responseError(res, new ServerError(ServerError.NotEnoughParam));
        return;
    }
    People.findOne({'userInfo.mail': mail}, function (err, people) {
        if (err) {
            ServicesUtil.responseError(res, err);
            return;
        } else  if (people) {
            ServicesUtil.responseError(res, ServerError.EmailAlreadyExist);
            return;
        }

        var people = new People({
            userInfo : {
                mail: mail,
                encryptedPassword : encryptedPassword
            }});
        people.save(function (err, people){
            if (err) {
                ServicesUtil.responseError(res, err);
                return;
            } else if (!people) {
                ServicesUtil.responseError(res, new ServerError(ServerError.ServerError));
                return;
            } else {
                var retData = {
                    metadata: {
                        //TODO change invilidateTime
//                        "invalidateTime": 3600000
                    },
                    data: {
                        people : people
                    }
                };
                res.json(retData);
            }
        });
    });
};

_update = function (req, res) {
//TODO add api for upload image
    var param;
    param = req.body;
    var curUser = req.currentUser;
    People.findOne({_id : curUser._id})
        .select('+userInfo')
        .exec(function (err, people) {
            var updateField = ['roles', 'name', 'portrait', 'height', 'weight',
                'gender', 'hairTypes'];
            updateField.forEach(function (field) {
                if (param[field]) {
                    people[field] = param[field];
                }
            });

            if (people.roles === 1) {
                if (param.status) {
                    people.status = param.status;
                }
            }
            var userInfoField = ['mail', 'encryptedPassword'];
            userInfoField.forEach(function (field) {
                if (param[field]) {
                    people.userInfo[field] = param[field];
                }
            });
            people.save(function (err, p) {
                try {
                    if (err) {
                        throw err;
                    }
                    delete p.userInfo.encryptedPassword;
                    var retData = {
                        metadata: {
                            //TODO change invilidateTime
                            "invalidateTime": 3600000
//                            "result" : 0
                        },
                        data: {
                            people: p
                        }
                    };
                    res.json(retData);
                } catch (e) {
                    ServicesUtil.responseError(res, e);
                    return;
                }
            });
        });
};

module.exports = {
    'login' : {method: 'post', func: _login},
    'logout' : {method: 'post', func: _logout, needLogin: true},
    'register' : {method: 'post', func: _register},
    'update' : {method: 'post', func: _update, needLogin: true}
};