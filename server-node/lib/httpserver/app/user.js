var mongoose = require('mongoose');
var async = require('async');
var uuid = require('node-uuid');
var path = require('path');
var jPushAudiences = require('../../dbmodels').JPushAudience;
var fs = require('fs');
var winston = require('winston');

var People = require('../../dbmodels').People;

var RequestHelper = require('../../helpers/RequestHelper');
var ResponseHelper = require('../../helpers/ResponseHelper');
var SMSHelper = require('../../helpers/SMSHelper');
var NotificationHelper = require('../../helpers/NotificationHelper');

var errors = require('../../errors');

var crypto = require('crypto'), _secret = 'qingshow@secret';
var moment =require('moment');

var request = require('request');
var WX_APPID = 'wx75cf44d922f47721';
var WX_SECRET = 'b2d418fcb94879affd36c8c3f37f1810';

var WB_APPID = 'wb1213293589';
var WB_SECRET = '';

var qsftp = require('../../runtime').ftp;

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
    if (!registrationId || registrationId.length === 0) {
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
    if (!registrationId || registrationId.length === 0) {
        return;
    }

    jPushAudiences.remove({
        'peopleRef' : peopleId,
        'registrationId' : registrationId
    }, function(err) {});
};

var _decryptMD5 = function (string){
    return crypto.createHash('md5')
    .update(string)
    .digest('hex')
    .toUpperCase();
}

var _get, _login, _logout, _update, _register, _updatePortrait, _updateBackground, _saveReceiver, _removeReceiver, _loginViaWeixin, _loginViaWeibo, _requestVerificationCode, _validateMobile, _resetPassword, _loginAsGuest;
_get = function(req, res) {
    async.waterfall([
    function(callback) {
        callback(req.qsCurrentUserId ? null : errors.NeedLogin);
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
            callback(errors.NeedLogin);
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
            }, {
                "mobile" : idOrNickName
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

            _addRegistrationId(people._id, param.registrationId);

            ResponseHelper.response(res, null, {
                people : people
            }, {
                "invalidateTime" : 3600000
            });
        } else {
            //login fail
            delete req.session.userId;
            delete req.session.loginDate;
            ResponseHelper.response(res, errors.IncorrectMailOrPassword);
        }
    });
};

_logout = function(req, res) {
    var id = RequestHelper.parseId(req.qsCurrentUserId);
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
    var mobile = param.mobile;
    var code = param.verificationCode;
    //TODO validate id and password
    if (!id || !password || !id.length || !password.length || !nickname || !mobile) {
        ResponseHelper.response(res, errors.NotEnoughParam);
        return;
    }
    People.find({
        '$or': [
            {'userInfo.id' : id}, 
            {'userInfo.id' : mobile}, 
            {'nickname': nickname}, 
            {'mobile': mobile}
        ]
    }, function(err, peoples) {
        if (err) {
            ResponseHelper.response(res, err);
            return;
        } else if (peoples.length > 0) {
            var verify = peoples.some(function(people){
                return people.nickname === nickname
            })
            if (verify) {
                 ResponseHelper.response(res, errors.NickNameAlreadyExist);   
            }else {
                ResponseHelper.response(res, errors.MobileAlreadyExist); 
            }
            return;
        }

        SMSHelper.checkVerificationCode(mobile, code, function(err, success){
            if (!success || err) {
                ResponseHelper.response(res, err);
            }
        });

        var people = new People({
            nickname: nickname,
            mobile : mobile,
            userInfo : {
                id : id,
                encryptedPassword : _encrypt(password)
            }
        });

        people.save(function(err, people) {
            if (err) {
                ResponseHelper.response(res, err);
            } else if (!people) {
                ResponseHelper.response(res, errors.genUnkownError());
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
                callback(errors.PeopleNotExist);
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
                    callback(errors.InvalidCurrentPassword);
                } else {
                    callback(err, people);
                }
            });
        } else {
            callback(null, people);
        }
    },
    function(people, callback) {
        if (qsParam.mobile) {
            People.find({
                'mobile' : qsParam.mobile
            }, function(err, peoples){
                if (peoples && peoples.length > 0) {
                    callback(errors.MobileAlreadyExist);
                }else {
                    callback(null, people);
                }
            });
        }else {
            callback(null, people);
        }
    },
    function(people, callback) {
        if (qsParam.nickname) {
            People.find({
                'nickname' : qsParam.nickname
            }, function(err, peoples){
                if (peoples && peoples.length > 0) {
                    callback(errors.NickNameAlreadyExist);
                }else {
                    callback(null, people);
                }
            });
        }else {
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
                callback(errors.PeopleNotExist);
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
        if (people.receivers === null || people.receivers.length === 0) {
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
                callback(errors.PeopleNotExist);
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

var _generateTempPathForHeadIcon = function (path) {
    var tempName = path.replace(/[\.\/:]/g, '_');
    var tempPath = "/tmp/" + tempName;
    return tempPath;
}

var _downloadHeadIcon = function (path, callback) {
    var tempPath = _generateTempPathForHeadIcon(path);

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
        ResponseHelper.response(res, errors.NotEnoughParam);
        return;
    }
    async.waterfall([function(callback) {
        var token_url = 'https://api.weixin.qq.com/sns/oauth2/access_token?appid=' + WX_APPID + '&secret=' + WX_SECRET + '&code=' + code + '&grant_type=authorization_code';
        winston.info('token url', token_url);
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
        winston.info('usr_url', usr_url);

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

        People.findOne({
            'userInfo.weixin.openid' : user.openid
        }, function(err, people) {
            if (err) {
                callback(err);
            } else {
                var shouldDownload = true;
                try {
                    if (people && people.userInfo.weixin.headimgurl === url) {
                        shouldDownload = false;
                    }
                } catch(e) {}
                callback(null, shouldDownload, user);
            }
        });

    },
    function (shouldDownloadHeadIcon, user, callback) {
        if (shouldDownloadHeadIcon) {
            //download headIcon
            _downloadHeadIcon(user.headimgurl, function (err, tempPath) {
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
                            var copyHeadPath = global.qsConfig.uploads.user.portrait.exposeToUrl + '/' + path.relative(config.uploads.user.portrait.ftpPath, newPath);
                            callback(err, user, copyHeadPath);
                        }
                    });
                }
            });
        } else {
            callback(null, user, "");
        }

    }, 
    function(weixinUser, copyHeadPath, callback) {
        People.findOne({
            'userInfo.weixin.openid' : weixinUser.openid
        }, function(err, people) {
            if (err) {
                callback(err);
            } else {
                if (!people) {
                    people = new People({
                        nickname : weixinUser.nickname,
                        userInfo : {
                            weixin : {
                                openid : weixinUser.openid,
                                nickname : weixinUser.nickname,
                                sex : weixinUser.sex,
                                province : weixinUser.province,
                                city : weixinUser.city,
                                country : weixinUser.country,
                                headimgurl : weixinUser.headimgurl,
                                unionid : weixinUser.unionid
                            }
                        }
                    });
                }

                if (copyHeadPath && copyHeadPath.length) {
                    people.portrait =copyHeadPath;
                }
                people.save(function(err, people) {
                    if (err) {
                        callback(err, people);
                    } else if (!people) {
                        callback(errors.genUnkownError());
                    } else {
                        callback(null, people);
                    }
                });
            }

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
    var config = global.qsConfig;
    var param = req.body;
    var token = param.access_token;
    var uid = param.uid;
    if (!token || !uid) {
        ResponseHelper.response(res, errors.NotEnoughParam);
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
    }, function (weiboUser, callback) {
        var url = weiboUser.avatar_large;

        People.findOne({
            'userInfo.weibo.id' : weiboUser.id
        }, function(err, people) {
            if (err) {
                callback(err);
            } else {
                var shouldDownload = true;
                try {
                    if (people && people.userInfo.weibo.avatar_large === url) {
                        shouldDownload = false;
                    }
                } catch(e) {}
                callback(null, shouldDownload, weiboUser);
            }
        });

    }, function (shouldDownloadHeadIcon, weiboUser, callback) {
        if (shouldDownloadHeadIcon) {
            var url = weiboUser.avatar_large;
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
                            var copyHeadPath = global.qsConfig.uploads.user.portrait.exposeToUrl + '/' + path.relative(config.uploads.user.portrait.ftpPath, newPath);
                            callback(err, weiboUser, copyHeadPath);
                        }
                    });
                }
            });
        } else {
            callback(null, weiboUser, "");
        }
    }, function(user, copyHeadPath, callback) {
        People.findOne({
            'userInfo.weibo.id' : user.id
        }, function(err, people) {
            if (err) {
                callback(err);
            } else {
                if (!people) {
                    people = new People({
                        nickname : user.screen_name,
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
                }

                if (copyHeadPath && copyHeadPath.length) {
                    people.portrait = copyHeadPath;
                }

                people.save(function(err, people) {
                    if (err) {
                        callback(err, people);
                    } else if (!people) {
                        callback(errors.genUnkownError());
                    } else {
                        callback(null, people);
                    }
                });
            }
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

_requestVerificationCode = function(req, res){
    var mobile = req.body.mobile;
    async.waterfall([function(callback){
        SMSHelper.createVerificationCode(mobile, function(err, code){
            if (err) {
                callback(err);
            }else {
                callback(null, code);
            }
        });
    },function(code, callback){
        var expire = global.qsConfig.verification.expire;
        SMSHelper.sendTemplateSMS(mobile, [code, expire/60/1000 + '分钟'], '36286', function(err, body){
            if (err) {
                callback(err);
            }else {
                callback(null, code);
            }
        });
    }],function(error, code) {
        ResponseHelper.response(res, error, {
        });
    });
};

_validateMobile = function(req, res){
    var params = req.body;
    var mobile = params.mobile;
    async.series([function(callback){
        var code = params.verificationCode;
        SMSHelper.checkVerificationCode(mobile, code, function(err, success){
            callback(err, success);
        });
    }],function(error, success) {
        ResponseHelper.response(res, error, {            
            'success' : success[0]
        });
    });
};

_resetPassword = function(req, res){
    var params = req.body;
    var mobile = params.mobile;
    var code = params.verificationCode;
    async.waterfall([function(callback){
        SMSHelper.checkVerificationCode(mobile, code, function(err, success){
            if (err) {
                callback(err);
            }else{
                callback(null, success);
            }
        });
    }, function(success, callback){
        People.find({
            'userInfo.id' : mobile
        }, function(err, peoples) {
            if (peoples.length > 1) {
                callback(errors.genUnkownError);
            }else {
                callback(null , peoples);
            }
        })
    }, function(people, callback) {
        var code = new Number(Math.random() * Math.pow(10,6)).toFixed(0);
        var tempPassword = _decryptMD5(code);
        People.findOneAndUpdate({
            'userInfo.id' : mobile
        }, {
            $unset : { 
                'userInfo.encryptedPassword' : -1
             },
             $set : {
                'userInfo.password' : tempPassword
             }
        }, {
        }, function(error, people) {
            if (error) {
                callback(errors.genUnkownError);
            }else {
                callback(null, tempPassword); 
            }
        });
    }],function(error, tempPassword) {
        ResponseHelper.response(res, error, {
            'password' : tempPassword
        });
    });
};


var _readNotification = function(req, res) {
    var params = req.body;
    var criteria = {};
    for (var element in params) {
        var key = 'extra.' + element;
        element === '_id' ? criteria[key] = RequestHelper.parseId(params._id) :
        criteria[key] = params[element];
    }

    NotificationHelper.read([req.qsCurrentUserId], criteria, function(error) {
        ResponseHelper.response(res, error, {});
    });
};

_loginAsGuest = function(req, res){
    var params = req.body;
    async.waterfall([function(callback){
        var nickname = '';
        var codeEnable = false;
        async.whilst(function() {
            return codeEnable;
        }, function(cb) {
            var code = (Math.random() * Math.pow(10, 6)).toFixed(0);
            nickname = 'u' + code;
            People.find({
                'nickname': nickname
            }, function(err, peoples) {
                if (err) {
                    cb(err);
                }else {
                   codeEnable = !(peoples && peoples.length > 0) ? true : false; 
                }
            })
        }, function(err) {
            err ? callback(errors.genUnkownError()) : callback(null, nickname);
        });
    }, function(nickname, callback){
        var people = new People();
        people.nickname = nickname;
        people.role = 'guest';
        people.save(callback);
    }],function(err, people){
        ResponseHelper.response(res, err, {
            'people' : people
        });
    });
}

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
    },
    'requestVerificationCode' : {
        method : 'post',
        func : _requestVerificationCode
    },
    'validateMobile' : {
        method : 'post',
        func : _validateMobile
    },
    'resetPassword' : {
        method : 'post',
        func : _resetPassword
    },
    'readNotification' : {
        method : 'post',
        permissionValidators : ['loginValidator'],
        func : _readNotification
    },
    '_loginAsGuest' : {
        method : 'post',
        func : _loginAsGuest
    }
};
