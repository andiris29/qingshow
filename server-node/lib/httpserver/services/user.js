var mongoose = require('mongoose');
var async = require('async');

var People = require('../../model/peoples');

var ResponseHelper = require('../helpers/ResponseHelper');
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

var _get, _login, _logout, _update, _register, _updatePortrait, _updateBackground;
_get = function(req, res) {
    async.waterfall([
    function(callback) {
        if (req.session.userId) {
            People.findOne({
                '_id' : req.session.userId
            }).select('userInfo.passwordUpdatedDate').exec(callback);
        } else {
            callback(ServerError.NeedLogin);
        }
    },
    function(people, callback) {
        if (!people || !people.userInfo) {
            callback(ServerError.SessionExpired);
        } else {
            var loginDate = req.session.loginDate;
            if (!people.userInfo.passwordUpdatedDate) {
                people.userInfo.passwordUpdatedDate = loginDate;
            }
            if (loginDate < people.userInfo.passwordUpdatedDate) {
                callback(ServerError.SessionExpired);
            } else {
                callback(null);
            }
        }
    },
    function(callback) {
        People.findOne({
            '_id' : req.session.userId
        }, callback);
    }], ResponseHelper.generateGeneralCallback(res, function(result) {
        return {
            'people' : result
        };
    }));
};

_login = function(req, res) {
    var param, id, password;
    param = req.body;
    id = param.id || '';
    password = param.password || '';
    People.findOne({
        "userInfo.id" : id,
        "$or" : [{
            "userInfo.password" : password
        }, {
            "userInfo.encryptedPassword" : _encrypt(password)
        }]
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
    delete req.qsCurrentUserId;
    var retData = {
        metadata : {
            "result" : 0
        }
    };
    res.json(retData);
};

_register = function(req, res) {
    var param, id, password;
    param = req.body;
    id = param.id;
    password = param.password;
    //TODO validate id and password
    if (!id || !password || !id.length || !password.length) {
        ServicesUtil.responseError(res, new ServerError(ServerError.NotEnoughParam));
        return;
    }
    People.findOne({
        'userInfo.id' : id
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
                id : id,
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
                req.session.userId = people._id;
                req.session.loginDate = new Date();

                var retData = {
                    metadata : {
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
    var param;
    param = req.body;
    People.findOne({
        _id : req.qsCurrentUserId
    }).select('+userInfo').exec(function(err, people) {
        var updateField = ['name', 'portrait', 'gender'];
        var numberField = ['height', 'weight'];
        var arrayField = ['roles', 'hairTypes'];
        updateField.forEach(function(field) {
            if (param[field]) {
                people[field] = param[field];
            }
        });
        numberField.forEach(function(field) {
            if (param[field]) {
                people[field] = parseFloat(param[field]);
            }
        });
        arrayField.forEach(function(field) {
            if (param[field]) {
                var fieldArray = param[field].split(',');
                fieldArray = fieldArray.filter(function(f) {
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
        if (param.id) {
            people.userInfo.id = param.id;
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

_updatePortrait = function(req, res) {
    _upload(req, res, 'portrait');
};

_updateBackground = function(req, res) {
    _upload(req, res, 'background');
};

var _upload = function(req, res, keyword) {
    var formidable = require('formidable');
    var path = require('path');

    var form = new formidable.IncomingForm();
    form.uploadDir = global.__qingshow_uploads.folder;
    form.keepExtensions = true;
    form.parse(req, function(err, fields, files) {
        if (err) {
            ServicesUtil.responseError(res, err);
            return;
        }
        var file;
        for (var key in files) {
            file = files[key];
        }
        People.findOne({
            '_id' : req.qsCurrentUserId
        }, function(err, people) {
            people.set(keyword, global.__qingshow_uploads.path + '/' + path.relative(form.uploadDir, file.path));
            people.save(function(err) {
                if (err) {
                    ServicesUtil.responseError(res, e);
                    return;
                }
                res.json({
                    data : {
                        people : people
                    }
                });
            });
        });
    });
    return;
};

module.exports = {
    'get' : {
        method : 'get',
        func : _get,
        permissionValidators : ['loginValidator']
    },
    'login' : {
        method : 'post',
        func : _login
    },
    'logout' : {
        method : 'post',
        func : _logout,
        permissionValidators : ['loginValidator']
    },
    'register' : {
        method : 'post',
        func : _register
    },
    'update' : {
        method : 'post',
        func : _update,
        permissionValidators : ['loginValidator']
    },
    'updatePortrait' : {
        method : 'post',
        func : _updatePortrait,
        permissionValidators : ['loginValidator']
    },
    'updateBackground' : {
        method : 'post',
        func : _updateBackground,
        permissionValidators : ['loginValidator']
    }
};
