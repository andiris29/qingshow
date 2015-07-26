var mongoose = require('mongoose');
var async = require('async');
var uuid = require('node-uuid');
var path = require('path');
var jPushAudiences = require('../../model/jPushAudiences');
var fs = require('fs');

var People = require('../../model/peoples');

var RequestHelper = require('../helpers/RequestHelper');
var ResponseHelper = require('../helpers/ResponseHelper');
var RequestHelper = require('../helpers/RequestHelper');

var ServerError = require('../server-error');

var crypto = require('crypto'), _secret = 'qingshow@secret';

var request = require('request');
var WX_APPID = 'wx75cf44d922f47721';
var WX_SECRET = 'b2d418fcb94879affd36c8c3f37f1810';

var WB_APPID = 'wb1213293589';
var WB_SECRET = '';

var qsftp = require('../../runtime/qsftp');

var _encrypt = function(string) {
    var cipher = crypto.createCipher('aes192', _secret);
    var enc = cipher.update(string, 'utf8', 'hex');
    enc += cipher.final('hex');
    return enc;
};

var userPortraitResizeOptions = [
    {'suffix' : '_200', 'width' : 200, 'height' : 200},
    {'suffix' : '_100', 'width' : 100, 'height' : 100},
    {'suffix' : '_50', 'width' : 50, 'height' : 50},
    {'suffix' : '_30', 'width' : 30, 'height' : 30}
];

var _decrypt = function(string) {
    var decipher = crypto.createDecipher('aes192', _secret);
    var dec = decipher.update(string, 'hex', 'utf8');
    dec += decipher.final('utf8');
    return dec;
};

var _addRegistrationId = function(peopleId, registrationId) {
    if (!registrationId || registrationId.length == 0) {
        return;
    }

    jPushAudiences.remove({
        'registrationId' : registrationId
    }, function(err) {
        if (err) {
            return;
        }

        var info = new jPushAudiences({
            'peopleRef' : peopleId,
            'registrationId' : registrationId
        });
        info.save();
    });
};

var _removeRegistrationId = function(peopleId, registrationId) {
    if (!registrationId || registrationId.length == 0) {
        return;
    }
    jPushAudiences.remove({
        'peopleRef' : peopleId,
        'registrationId' : registrationId
    });
};

var _get, _login, _logout, _update, _register, _updatePortrait, _updateBackground, _saveReceiver, _removeReceiver, _loginViaWeixin, _loginViaWeibo;
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
    var param, idOrNickName, password;
    param = req.body;
    idOrNickName = param.idOrNickName || '';
    password = param.password || '';
    People.findOne({
        "$and" : [{
            "$or" : [{
                "userInfo.id" : idOrNickName
            }, {
                "nickname" : idOrNickName
            }]}, {
            "$or" : [{
                "userInfo.password" : password
            }, {
                "userInfo.encryptedPassword" : _encrypt(password)
            }]}
        ]
    }).exec(function(err, people) {
        if (err) {
            ResponseHelper.response(res, err);
        } else if (people) {
            //login succeed
            req.session.userId = people._id;
            req.session.loginDate = new Date();

            people.jPushInfo = _addRegistrationId(people._id, param.registrationId);

            people.save(function(err, people) {
                if (err) {
                    ResponseHelper.response(res, err);
                } else {
                    var retData = {
                        metadata : {
                            "invalidateTime" : 3600000
                        },
                        data : {
                            people : people
                        }
                    };
                    res.json(retData);
                }
            });
        } else {
            //login fail
            delete req.session.userId;
            delete req.session.loginDate;
            ResponseHelper.response(res, ServerError.IncorrectMailOrPassword);
        }
    });
};

_logout = function(req, res) {
    var id = req.qsCurrentUserId;
    _removeRegistrationId(id, req.body.registrationId);
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
    if (!id || !password || !id.length || !password.length || !nickname) {
        ResponseHelper.response(res, ServerError.NotEnoughParam);
        return;
    }
    People.find({
        '$or': [{'userInfo.id' : id}, {'nickname': nickname}]
    }, function(err, people) {
        if (err) {
            ResponseHelper.response(res, err);
            return;
        } else if (people.length > 0) {
            ResponseHelper.response(res, ServerError.EmailAlreadyExist);
            return;
        }

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

                _addRegistrationId(people._id, req.body.registrationId);

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
    qsParam = req.body;
    async.waterfall([
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

        if (qsParam.measureInfo) {
            if (qsParam.measureInfo.shoulder) {
                people.set('measureInfo.shoulder', RequestHelper.parseNumber(qsParam.measureInfo.shoulder));
            }
            if (qsParam.measureInfo.bust) {
                people.set('measureInfo.bust', RequestHelper.parseNumber(qsParam.measureInfo.bust));
            }
            if (qsParam.measureInfo.waist) {
                people.set('measureInfo.waist', RequestHelper.parseNumber(qsParam.measureInfo.waist));
            }
            if (qsParam.measureInfo.hips) {
                people.set('measureInfo.hips', RequestHelper.parseNumber(qsParam.measureInfo.hips));
            }
            if (qsParam.measureInfo.shoeSize) {
                people.set('measureInfo.shoeSize', RequestHelper.parseNumber(qsParam.measureInfo.shoeSize));

            }

            delete qsParam.measureInfo;
        }

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
    _upload(req, res, global.qsConfig.uploads.user.portrait, 'portrait', userPortraitResizeOptions);
};

_updateBackground = function(req, res) {
    _upload(req, res, global.qsConfig.uploads.user.background, 'background');
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

var _upload = function(req, res, config, keyword, resizeOptions) {
    RequestHelper.parseFile(req, config.ftpPath, resizeOptions, function(err, fields, file) {
        if (err) {
            ResponseHelper.response(res, err);
            return;
        }
        People.findOne({
            '_id' : req.qsCurrentUserId
        }, function(err, people) {
            people.set(keyword, config.exposeToUrl + '/' + path.relative(config.ftpPath, file.path));
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


var _downloadHeadIcon = function (path, callback) {
    var tempName = path.replace(/[\.\/:]/g, '_');
    var tempPath = "/tmp/" + tempName;

    request(path).pipe(fs.createWriteStream(tempPath))
        .on('close', function () {
            callback(null, tempPath);
        })
        .on('error', function (err) {
            callback(err);
        });
};

_loginViaWeixin = function(req, res) {
    var config = global.qsConfig;
    var param = req.body;
    var code = param.code;
    if (!code) {
        ResponseHelper.response(res, ServerError.NotEnoughParam);
        return;
    }
    async.waterfall([function(callback) {
        var token_url = 'https://api.weixin.qq.com/sns/oauth2/access_token?appid=' + WX_APPID + '&secret=' + WX_SECRET + '&code=' + code + '&grant_type=authorization_code';
        request.get(token_url, function(error, response, body) {
            var data = JSON.parse(body);
            if (data.errcode !== undefined) {
                callback(data);
                return;
            }
            callback(null, data.access_token, data.openid);
        });
    }, function(token, openid, callback) {
        var usr_url = 'https://api.weixin.qq.com/sns/userinfo?access_token=' + token + '&openid=' + openid;

        request.get(usr_url, function(errro, response, body) {
            var data = JSON.parse(body);
            if (data.errorcode !== undefined) {
                callback({
                    errorcode : data.errcode,
                    weixin_err : data
                });
                return;
            }

            callback(null, {
                'openid' : data.openid,
                'nickname' : data.nickname,
                'sex' : data.sex,
                'province' : data.province,
                'city' : data.city,
                'country' : data.country,
                'headimgurl' : data.headimgurl,
                'privilege' : data.privilege,
                'unionid' : data.unionid
            });
        });
    }, function (user, callback) {
        var url = user.headimgurl;
        //download headIcon
        _downloadHeadIcon(url, function (err, tempPath) {
            if (err) {
                callback(err);
            } else {
                //update head icon to ftp
                var baseName = path.basename(tempPath);
                qsftp.uploadWithResize(tempPath, baseName, global.qsConfig.uploads.user.portrait.ftpPath, userPortraitResizeOptions, function (err) {
                    if (err) {
                        callback(err);
                    } else {
                        var newPath = path.join(global.qsConfig.uploads.user.portrait.ftpPath, baseName);
                        user.headimgurl =  global.qsConfig.uploads.user.portrait.exposeToUrl + '/' + path.relative(config.uploads.user.portrait.ftpPath, newPath);
                        callback(err);
                    }
                })
            }


        });
    }, function(user, callback) {
        People.findOne({
            'userInfo.weixin.openid' : user.openid
        }, function(err, people) {
            if (err) {
                callback(err);
                return;
            } else if (people !== null) {
                callback(null, people);
                return;
            }

            people = new People({
                nickname : user.nickname,
                portrait : user.headimgurl,
                userInfo : {
                    weixin : {
                        openid : user.openid,
                        nickname : user.nickname,
                        sex : user.sex,
                        province : user.province,
                        city : user.city,
                        country : user.country,
                        headimgurl : user.headimgurl,
                        unionid : user.unionid
                    }
                }
            });

            people.save(function(err, people) {
                if (err) {
                    callback(err, people);
                } else if (!people) {
                    callback(ServerError.ServerError);
                } else {
                    callback(null, people);
                }
            });
        });
    }, function(people, callback) {
        req.session.userId = people._id;
        req.session.loginDate = new Date();
        _addRegistrationId(people._id, param.registrationId);
        callback(null, people);
    }], function(error, people) {
        ResponseHelper.response(res, error, {
            'people' : people
        });
    });
};

_loginViaWeibo = function(req, res) {
    var param = req.body;
    var token = param.access_token;
    var uid = param.uid;
    if (!token || !uid) {
        ResponseHelper.response(res, ServerError.NotEnoughParam);
        return;
    }
    async.waterfall([function(callback) {
        var url = "https://api.weibo.com/2/users/show.json?access_token=" + token + "&uid=" + uid;
        request.get(url, function(error, response, body) {
            var data = JSON.parse(body);
            if (data.error !== undefined) {
                callback(data);
                return;
            }

            callback(null, {
                id : data.id,
                screen_name : data.screen_name,
                province : data.province,
                country : data.country,
                gender : data.gender,
                avatar_large : data.avatar_large
            });
        });
    }, function(user, callback) {
        People.findOne({
            'userInfo.weibo.id' : user.id
        }, function(err, people) {
            if (err) {
                callback(err);
                return;
            } else if (people !== null) {
                callback(null, people);
                return;
            }

            people = new People({
                nickname : user.screen_name,
                portrait : user.avatar_large,
                userInfo : {
                    weibo: {
                        id : user.id,
                        screen_name : user.screen_name,
                        province : user.province,
                        country : user.country,
                        gender : user.gender,
                        avatar_large : user.avatar_large 
                    }
                }
            });

            people.save(function(err, people) {
                if (err) {
                    callback(err, people);
                } else if (!people) {
                    callback(ServerError.ServerError);
                } else {
                    callback(null, people);
                }
            });
        });
    }, function(people, callback) {
        req.session.userId = people._id;
        req.session.loginDate = new Date();
        _addRegistrationId(people._id, param.registrationId);
        callback(null, people);
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
    },
    'loginViaWeixin' : {
        method : 'post',
        func : _loginViaWeixin
    },
    'loginViaWeibo' : {
        method : 'post',
        func : _loginViaWeibo
    }
};
