var People = require('../../model/peoples');
var ServicesUtil = require('../servicesUtil');
var ServerError = require('../server-error');
var crypto = require('crypto'), _secret = 'qingshow@secret';

var _encrypt = function(string) {
    var cipher = crypto.createCipher('aes192', _secret);
    var enc = cipher.update(string, 'utf8', 'hex');
    enc += cipher.final('hex');
    return enc;
};

var _decrypt = function(string) {
    var decipher = crypto.createDecipher('aes192', _secret);
    var dec = decipher.update(string, 'hex', 'utf8');
    dec += decipher.final('utf8');
    return dec;
};

var _get, _login, _logout, _update, _register;
_get = function(req, res) {
    res.json({
        'data' : {
            'people' : req.currentUser
        }
    });
};

_login = function(req, res) {
    var param, mail, password;
    param = req.body;
    mail = param.mail || '';
    password = param.password || '';
    People.findOne({
        "userInfo.mail" : mail,
        "userInfo.encryptedPassword" : _encrypt(password)
    }).select("+userInfo").exec(function(err, people) {
        if (err) {
            ServicesUtil.responseError(res, err);
        } else if (people) {
            //login succeed
            req.session.userId = people._id;
            req.session.loginDate = new Date();

            var retData = {
                metadata : {
                    //TODO change invilidateTime
                    "invalidateTime" : 3600000
                },
                data : {
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

_logout = function(req, res) {
    delete req.session.userId;
    delete req.session.loginDate;
    delete req.currentUser;
    var retData = {
        metadata : {
            "result" : 0
        }
    };
    res.json(retData);
};

_register = function(req, res) {
    var param, mail, password;
    param = req.body;
    mail = param.mail;
    password = param.password;
    //TODO validate mail and password
    if (!mail || !password || !mail.length || !password.length) {
        ServicesUtil.responseError(res, new ServerError(ServerError.NotEnoughParam));
        return;
    }
    People.findOne({
        'userInfo.mail' : mail
    }, function(err, people) {
        if (err) {
            ServicesUtil.responseError(res, err);
            return;
        } else if (people) {
            ServicesUtil.responseError(res, new ServerError(ServerError.EmailAlreadyExist));
            return;
        }

        var people = new People({
            userInfo : {
                mail : mail,
                encryptedPassword : _encrypt(password)
            }
        });
        people.save(function(err, people) {
            if (err) {
                ServicesUtil.responseError(res, err);
                return;
            } else if (!people) {
                ServicesUtil.responseError(res, new ServerError(ServerError.ServerError));
                return;
            } else {
                var retData = {
                    metadata : {
                        //TODO change invilidateTime
                        //                        "invalidateTime": 3600000
                    },
                    data : {
                        people : people
                    }
                };
                res.json(retData);
            }
        });
    });
};

_update = function(req, res) {
    //TODO add api for upload image
    var param;
    param = req.body;
    var curUser = req.currentUser;
    People.findOne({
        _id : curUser._id
    }).select('+userInfo').exec(function (err, people) {
        var updateField = ['name', 'portrait', 'gender'];
        var numberField = ['height', 'weight'];
        var arrayField = ['roles', 'hairTypes'];
        updateField.forEach(function (field) {
            if (param[field]) {
                people[field] = param[field];
            }
        });
        numberField.forEach(function (field) {
            if (param[field]) {
                people[field] = parseFloat(param[field]);
            }
        });
        arrayField.forEach(function (field) {
            if (param[field]) {
                var fieldArray = field.split(',');
                fieldArray = fieldArray.filter(function (f){
                    return f && f.length !== 0;
                });
                people[field] = fieldArray;
            }
        });

        if (people.roles === 1) {
            if (param.status) {
                people.status = param.status;
            }
        }
        if (param.mail) {
            people.userInfo.mail = param.mail;
        }
        //TODO: check param.currentPassword
        if (param.password) {
            people.userInfo.encryptedPassword = _encrypt(param.password);
        }
        people.save(function(err, p) {
            try {
                if (err) {
                    throw err;
                }
                delete p.userInfo.encryptedPassword;
                var retData = {
                    metadata : {
                        //TODO change invilidateTime
                        "invalidateTime" : 3600000
                        //                            "result" : 0
                    },
                    data : {
                        people : p
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
    'get' : {
        method : 'get',
        func : _get,
        needLogin : true
    },
    'login' : {
        method : 'post',
        func : _login
    },
    'logout' : {
        method : 'post',
        func : _logout,
        needLogin : true
    },
    'register' : {
        method : 'post',
        func : _register
    },
    'update' : {
        method : 'post',
        func : _update,
        needLogin : true
    }
};
