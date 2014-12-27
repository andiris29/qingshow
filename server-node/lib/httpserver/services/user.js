var mongoose = require('mongoose');
var async = require('async');

var People = require('../../model/peoples');

var RequestHelper = require('../helpers/RequestHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var RequestHelper = require('../helpers/RequestHelper');

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
        if (req.qsCurrentUserId) {
            People.findOne({
                '_id' : req.qsCurrentUserId
            }, function(err, people) {
                if (err) {
                    callback(ServerError.fromError(err));
                } else if (people) {
                    callback(null, people);
                } else {
                    callback(ServerError.fromCode(ServerError.NeedLogin));
                }
            });
        } else {
            callback(ServerError.NeedLogin);
        }
    }], function(err, people) {
        ResponseHelper.response(res, err, {
            'people' : people
        });
    });
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
    }).exec(function(err, people) {
        if (err) {
            ResponseHelper.response(res, err);
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
            ResponseHelper.response(res, ServerError.IncorrectMailOrPassword);
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
        ResponseHelper.response(res, ServerError.NotEnoughParam);
        return;
    }
    People.findOne({
        'userInfo.id' : id
    }, function(err, people) {
        if (err) {
            ResponseHelper.response(res, err);
            return;
        } else if (people) {
            ResponseHelper.response(res, ServerError.EmailAlreadyExist);
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
                ResponseHelper.response(res, err);
                return;
            } else if (!people) {
                ResponseHelper.response(res, ServerError.ServerError);
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
    var qsParam;
    async.waterfall([
    function(callback) {
        try {
            qsParam = {};
            ['name', 'portrait', 'gender', 'password', 'currentPassword'].forEach(function(field) {
                if (req.body[field]) {
                    qsParam[field] = req.body[field];
                }
            });
            ['height', 'weight'].forEach(function(field) {
                if (req.body[field]) {
                    qsParam[field] = parseFloat(req.body[field]);
                }
            });
            ['roles', 'hairTypes'].forEach(function(field) {
                if (req.body[field]) {
                    qsParam[field] = RequestHelper.parseArray(req.body[field]);
                }
            });
        } catch(err) {
            callback(err);
            return;
        }
        callback();
    },
    function(callback) {
        People.findOne({
            '_id' : req.qsCurrentUserId
        }, function(err, people) {
            if (!err && !people) {
                callback(ServerError.PeopleNotExist);
            } else {
                callback(err, people);
            }
        });
    },
    function(people, callback) {
        if (qsParam.password) {
            People.findOne({
                '_id' : req.qsCurrentUserId,
                "$or" : [{
                    'userInfo.password' : qsParam.currentPassword
                }, {
                    'userInfo.encryptedPassword' : _encrypt(qsParam.currentPassword)
                }]
            }, function(err, people) {
                if (!err && !people) {
                    callback(ServerError.InvalidCurrentPassword);
                } else {
                    callback(err, people);
                }
            });
        } else {
            callback(null, people);
        }
    },
    function(people, callback) {
        if (qsParam.password) {
            people.set('userInfo.password', undefined);
            people.set('userInfo.encryptedPassword', _encrypt(qsParam.password));
        }
        delete qsParam.password;
        delete qsParam.currentPassword;
        for (var field in qsParam) {
            people.set(field, qsParam[field]);
        }
        people.save(callback);
    }], function(err, people) {
        ResponseHelper.response(res, err, {
            'people' : people
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
            ResponseHelper.response(res, err);
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
                    ResponseHelper.response(res, err);
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
