var People = require('../../model/peoples');
var ServicesUtil = require('../servicesUtil');
var ServerError = require('../server-error');

var _login, _update;
_login = function (req, res) {
    var param, mail, encryptedPassword;
    param = req.body;
    mail = param.mail || '';
    encryptedPassword = param.encryptedPassword || '';
    People.findOne({"userInfo.mail" : mail, "userInfo.encryptedPassword": encryptedPassword}, function (err, people) {
        try {
            if (err) {
                throw err;
            }
            if (people) {
                //login succeed
                req.session.userId = people._id;
                req.session.loginDate = new Date();
                res.json(people);
            } else {
                //login fail
                delete req.session.userId;
                delete req.session.loginDate;
                throw new ServerError(ServerError.IncorrectMailOrPassword);
            }
        } catch (e) {
            ServicesUtil.responseError(res, e);
        }
    });
};

_update = function (req, res) {

    var param;
    param = req.body;
    var people = req.currentUser;
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
    people.save(function (err, people) {
        try {
            if (err) {
                throw err;
            }
            var retData = {
                metadata: {
                    //TODO change invilidateTime
                    "invalidateTime": 3600000
                },
                data: people
            };
            res.json(retData);
        } catch (e) {
            ServicesUtil.responseError(res, e);
        }
    });
};


module.exports = {
    'login' : {method: 'post', func: _login},
    'update' : {method: 'post', func: _update, needLogin: true}
};