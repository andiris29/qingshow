var mongoose = require('mongoose');
var async = require('async');
var uuid = require('node-uuid');

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

var _get, _login, _logout, _update, _register, _updatePortrait, _updateBackground, _saveReceiver, _removeReceiver;
_get = function(req, res) {
    async.waterfall([
    function(callback) {
        callback(req.qsCurrentUserId ? null : ServerError.NeedLogin);
    },
    function(callback) {
        People.findOne({
            '_id' : req.qsCurrentUserId
        }, callback);
    },
    function(people, callback) {
        if (people) {
            if (req.queryString.assetsRoot) {
                people.assetsRoot = req.queryString.assetsRoot;
                people.save(function() {
                    callback(null, people);
                });
            } else {
                callback(null, people);
            }
        } else {
            callback(ServerError.fromCode(ServerError.NeedLogin));
        }
    },
    function(people, callback) {
        People.findOne({
            'assetsRoot' : {
                '$ne' : null
            }
        }, function(err, tplt) {
            callback(err, people, tplt);
        });
    },
    function(people, tplt, callback) {
        if (tplt) {
            req.session.assetsRoot = tplt.assetsRoot;
        }
        callback(null, people);
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
    var nickname = param.nickname;
    //TODO validate id and password
    if (!id || !password || !id.length || !password.length) {
        ResponseHelper.response(res, ServerError.NotEnoughParam);
        return;
    }
    People.find({
        $or: [{'userInfo.id' : id}, {'nickname': nickname}]
    }, function(err, people) {
        if (err) {
            ResponseHelper.response(res, err);
            return;
        } else if (people) {
            ResponseHelper.response(res, ServerError.EmailAlreadyExist);
            return;
        }
        require('../../runtime/qsmail').debug('New user: ' + id, [id, password].join(','), function(err, info) {
        });

        var people = new People({
            nickname: nickname,
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
            qsParam = RequestHelper.parse(req.body, {
                'birthday' : RequestHelper.parseDate
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

_saveReceiver = function(req, res) {
    var param = req.body;
    async.waterfall([function(callback) {
        People.findOne({
            '_id' : req.qsCurrentUserId
        }, function(error, people) {
            if (!error && !people) {
                callback(ServerError.PeopleNotExist);
            } else {
                callback(error, people);
            }
        });
    }, function(people, callback) {
        var receiver = {};
        for(var element in param) {
            receiver[element] = param[element];
        }
        if (!receiver.isDefault) {
            receiver.isDefault = false;
        }
        if (people.receivers == null || people.receivers.length == 0) {
            people.receivers = [];
            receiver.isDefault = true;
            if (!receiver.uuid) {
                receiver.uuid = uuid.v1();
            }
            people.receivers.push(receiver);
        } else {
            var hit = -1;
            // find index
            for (var i = 0; i < people.receivers.length; i++) {
                var element = people.receivers[i];
                if (!receiver.uuid) {
                    if (element.name == receiver.name && element.phone == receiver.phone && element.province == receiver.province && element.address == receiver.address) {
                        hit = i;
                        break;
                    }
                } else {
                    if (element.uuid == receiver.uuid) {
                        hit = i;
                        break;
                    }
                }
            }

            if (receiver.isDefault) {
                for (var i = 0; i < people.receivers.length; i++) {
                    people.receivers[i].isDefault = false;
                }
            }

            if (!receiver.uuid) {
                receiver.uuid = uuid.v1();
            }
            if (hit == -1) {
                people.receivers.push(receiver);
            } else {
                for(var field in receiver) {
                    people.receivers[hit].set(field, receiver[field]);
                }
            }
        }

        people.save(function(error, people) {
            callback(error, people, receiver.uuid);
        });
    }], function(error, people, nowUuid) {
        ResponseHelper.response(res, error, {
            'people' : people,
            'receiverUuid' : nowUuid 
        });
    });
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

_removeReceiver = function(req, res) {
    var param = req.body;
    async.waterfall([function(callback) {
        People.findOne({
            '_id' : req.qsCurrentUserId
        }).exec(function(error, people) {
            if (!error && !people) {
                callback(ServerError.PeopleNotExist);
            } else {
                callback(error, people);
            }
        });
    }, function(people, callback) {
        var receivers = people.receivers;
        var index = -1;
        var isDefault = false;
        if (receivers != null) {
            for(var i = 0; i < receivers.length; i++) {
                if (receivers[i].uuid == param.uuid) {
                    index = i;
                    isDefault = receivers[i].isDefault;
                    break;
                }
            }

            if (index > -1) {
                people.receivers.splice(index, 1);
                if (isDefault && people.receivers.length > 0) {
                    people.receivers[0].isDefault = true;
                }
            }
        }
        people.save(callback);
    }], function(error, people) {
        ResponseHelper.response(res, error, {
            'people' : people
        });
    });
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
    },
    'saveReceiver' : {
        method : 'post',
        func : _saveReceiver,
        permissionValidators : ['loginValidator']
    },
    'removeReceiver' : {
        method : 'post',
        func : _removeReceiver,
        permissionValidators : ['loginValidator']
    }
};
