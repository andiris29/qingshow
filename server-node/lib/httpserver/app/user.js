var mongoose = require('mongoose');
var async = require('async');
var uuid = require('node-uuid');
var path = require('path');
var jPushAudiences = require('../../dbmodels').JPushAudience;
var fs = require('fs');
var winston = require('winston');
var TraceHelper = require('../../helpers/TraceHelper');

var People = require('../../dbmodels').People;

var RequestHelper = require('../../helpers/RequestHelper');
var ResponseHelper = require('../../helpers/ResponseHelper');
var SMSHelper = require('../../helpers/SMSHelper');
var NotificationHelper = require('../../helpers/NotificationHelper');

var VersionUtil = require('../../utils/VersionUtil');

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

var _get, _login, _logout, _update, _register, _updatePortrait, _updateBackground, _saveReceiver, _removeReceiver, _loginViaWeixin, _loginViaWeibo, _requestVerificationCode, _validateMobile, _resetPassword, _loginAsGuest, _updateRegistrationId, _loginAsViewer;

_get = [
    require('../middleware/injectCurrentUser'),
    function(req, res, next) {
        ResponseHelper.writeData(res, {
            'people' : req.qsCurrentUser
        });
        next();
    }
];

_login = [function(req, res, next) {
    // Upgrade the req
    var v = RequestHelper.getVersion(req);
    if (VersionUtil.lt(v, '2.2.0')) {
        req.body.id = req.body.idOrNickName;
    }
    next();
}, function(req, res, next) {
    // Implementation
    var param = req.body,
        id = param.id || '',
        password = param.password || '';
    People.findOne({
        "$or" : [{"userInfo.id" : id}, 
            {"mobile" : id}],
        "$or" : [{ "userInfo.password" : password}, {
            "userInfo.encryptedPassword" : _encrypt(password)}]
    }).exec(function(err, people) {
        if (err) {
            next(errors.genUnkownError(err));
        } else if (people) {
            //login succeed
            req.session.userId = people._id;
            req.session.loginDate = new Date();

            _addRegistrationId(people._id, param.registrationId);

            ResponseHelper.write(res, {
                'invalidateTime' : 3600000
            }, {
                'people' : people
            });
            next();
        } else {
            //login fail
            delete req.session.userId;
            delete req.session.loginDate;
            
            next(errors.IncorrectMailOrPassword);
        }
    });
}];

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

_register = [
    require('../middleware/injectCurrentUser'),
    function(req, res, next) {
        if (req.qsCurrentUser.role !== 0) {
            next(errors.AlreadyLoggedIn);
        } else {
            next();
        }
    }, function(req, res, next) {
        // Validate whether the id/mobile is already existed
        People.findOne({
            '_id' : {'$ne' : req.qsCurrentUserId},
            '$or': [{'userInfo.id' : req.body.id}, 
                {'mobile': req.body.mobile}]
        }, function(err, people) {
            if (err) {
                next(errors.genUnkownError(err));
            } else {
                if (people) {
                    // Replace current user with db.people
                    req.qsCurrentUser = people;
                    req.qsCurrentUserId = people._id;
                }
                next();
            }
        });
    }, function(req, res, next) {
        SMSHelper.checkVerificationCode(req, req.body.mobile, req.body.verificationCode, function(err, success){
            if (!success || err) {
                next(err);
            } else {
                next();
            }
        });
    }, function(req, res, next) {
        var people = req.qsCurrentUser;
        people.role = 1;
        people.mobile = req.body.mobile;
        people.userInfo = {
            'id' : req.body.id,
            'encryptedPassword' : _encrypt(req.body.password)
        };
        people.save(function(err, people) {
            if (!people || err) {
                next(errors.genUnkownError(err));
            } else {
                req.session.userId = people._id;
                req.session.loginDate = new Date();

                _addRegistrationId(people._id, req.body.registrationId);

                ResponseHelper.writeData(res, {
                    'people' : people
                });
                next();
            }
        });
    }
];

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
                'nickname' : qsParam.nickname,
                '_id' : {
                    '$ne' : req.qsCurrentUserId
                }
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
    async.waterfall([function(callback){
        People.findOne({
            '_id' : req.qsCurrentUserId
        }, callback);
    }, function(people, callback){
        RequestHelper.parseFile(req, config.ftpPath, people._id.toString(), resizeOptions, function(err, fields, file) {
            if (err) {
                callback(err);
                return;
            }
            people.set(keyword, config.exposeToUrl + '/' + path.relative(config.ftpPath, file.path));
            people.save(function(err, people) {
                callback(err, people);
            });
        });
    }], function(err, people){
        ResponseHelper.response(res, err, {
            people : people
        });
    });
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
                callback(null, shouldDownload, user, people);
            }
        });

    },
    function (shouldDownloadHeadIcon, user, people, callback) {
        if (shouldDownloadHeadIcon) {
            //download headIcon
            _downloadHeadIcon(user.headimgurl, function (err, tempPath) {
                if (err) {
                    callback(err);
                    try {
                        fs.unlink(tempPath, function(){});
                    } catch (e) {
                    }
                } else {
                    //update head icon to ftp
                    var baseName = people._id.toString();
                    qsftp.uploadWithResize(tempPath, baseName, global.qsConfig.uploads.user.portrait.ftpPath, userPortraitResizeOptions, function (err) {
                        if (err) {
                            callback(err);
                        } else {
                            var newPath = path.join(global.qsConfig.uploads.user.portrait.ftpPath, baseName);
                            var copyHeadPath = global.qsConfig.uploads.user.portrait.exposeToUrl + '/' + path.relative(config.uploads.user.portrait.ftpPath, newPath);
                            callback(err, user, copyHeadPath);
                        }
                        try {
                            fs.unlink(tempPath, function() {});
                        } catch (e) {
                        }
                    });
                }
            });
        } else {
            callback(null, user, "");
        }

    }, 
    function(weixinUser, copyHeadPath, callback){
         People.findOne({
            'userInfo.weixin.openid' : weixinUser.openid
        }, function(err, people) {
            callback(null, people, weixinUser, copyHeadPath);
        });
    }, function(people, weixinUser, copyHeadPath, callback){
        if (!people) {
            if (req.qsCurrentUserId) {
                People.findOne({
                    '_id': req.qsCurrentUserId
                }, function(err, target){
                    callback(null, target, weixinUser, copyHeadPath);
                })
            }else{
                callback(null, new People(), weixinUser, copyHeadPath);
            }
        }else {
            callback(null, people, weixinUser, copyHeadPath);
        }
    },function(people, weixinUser, copyHeadPath, callback){
        people.nickname = weixinUser.nickname;
        people.userInfo = {
            weixin: {
                openid: weixinUser.openid,
                nickname: weixinUser.nickname,
                sex: weixinUser.sex,
                province: weixinUser.province,
                city: weixinUser.city,
                country: weixinUser.country,
                headimgurl: weixinUser.headimgurl,
                unionid: weixinUser.unionid
            }
        };
        
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
                callback(null, shouldDownload, weiboUser, people);
            }
        });

    }, function (shouldDownloadHeadIcon, weiboUser, people, callback) {
        if (shouldDownloadHeadIcon) {
            var url = weiboUser.avatar_large;
            //download headIcon
            _downloadHeadIcon(url, function (err, tempPath) {
                if (err) {
                    callback(err);
                    try {
                        fs.unlink(tempPath, function () {});
                    } catch (e) {
                    }
                } else {
                    //update head icon to ftp
                    var baseName = people._id.toString();
                    qsftp.uploadWithResize(tempPath, baseName, global.qsConfig.uploads.user.portrait.ftpPath, userPortraitResizeOptions, function (err) {
                        if (err) {
                            callback(err);
                        } else {
                            var newPath = path.join(global.qsConfig.uploads.user.portrait.ftpPath, baseName);
                            var copyHeadPath = global.qsConfig.uploads.user.portrait.exposeToUrl + '/' + path.relative(config.uploads.user.portrait.ftpPath, newPath);
                            callback(err, weiboUser, copyHeadPath);
                        }
                        try {
                            fs.unlink(tempPath, function() {});
                        } catch (e) {
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
            callback(null, people, user, copyHeadPath);
        });
    }, function(people, user, copyHeadPath, callback){
        if (!people) {
            if (req.qsCurrentUserId) {
                People.findOne({
                    '_id' : req.qsCurrentUserId
                }, function(err, target){
                    callback(null, target, user, copyHeadPath);
                })
            }else{
                callback(null, new People(), user, copyHeadPath);
            }   
        }else {
            callback(null, people, user, copyHeadPath);
        }
    }, function(people, user, copyHeadPath, callback){
        people.nickname = user.screen_name;
        people.userInfo = {
            weibo: {
                id: user.id,
                screen_name: user.screen_name,
                province: user.province,
                country: user.country,
                gender: user.gender,
                avatar_large: user.avatar_large
            }
        };
        
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
        console.log(req);
        SMSHelper.createVerificationCode(req, mobile, function(err, code){
            if (err) {
                callback(err);
            }else {
                callback(null, code);
            }
        });
    },function(code, callback){
        var expire = global.qsConfig.verification.expire;
        SMSHelper.sendTemplateSMS(req, mobile, [code, expire/60/1000 + '分钟'], '36286', function(err, body){
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
        SMSHelper.checkVerificationCode(req, mobile, code, function(err, success){
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
        SMSHelper.checkVerificationCode(req, mobile, code, function(err, success){
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
        async.until(function() {
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
                   cb();
                }
            });
        }, function(err) {
            err ? callback(errors.genUnkownError()) : callback(null, nickname);
        });
    }, function(nickname, callback){
        var people = new People();
        people.nickname = nickname;
        people.role = 0;
        people.save(function(err, people){
            _addRegistrationId(people._id, params.registrationId);
            req.session.userId = people._id;
            req.session.loginDate = new Date();
            callback(null, people);
        });
    }],function(err, people){
        ResponseHelper.response(res, err, {
            'people' : people
        });

        TraceHelper.trace('behavior-loginAsGuest', req, {
            '_id' : people._id
        });
    });
}

_updateRegistrationId = function(req, res){
    var params = req.body;
    var registrationId = params.registrationId;
    People.findOne({
        '_id': req.qsCurrentUserId
    }, function(err, people) {
        _addRegistrationId(people._id, registrationId)
        ResponseHelper.response(res, err, {
            'people' : people
        });
    });
}

_loginAsViewer = function(req, res){
    var params = req.body;
    async.waterfall([function(callback){
        People.findOne({
            'userInfo.id' : RequestHelper.getIp(req)
        }, callback);
    }, function(people, callback){
        if (people) {
            callback(null, people);
        }else{
            var people = new People({
                'role' : 2,
                'userInfo.id' : RequestHelper.getIp(req)
            });
            req.invitorRef = people.invitorRef = params.invitorRef;
            people.save(function(err, people){
                if (err) {
                    callback(errors.genUnkownError);
                }else{
                    callback(null, people);
                }
            })
        }
    }], function(err, people){
        ResponseHelper.response(res, err, {
            'people' : people
        });
    })
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
    'loginAsGuest' : {
        method : 'post',
        func : _loginAsGuest
    },
    'updateRegistrationId' : {
        method : 'post',
        permissionValidators : ['loginValidator'],
        func : _updateRegistrationId
    },
    'loginAsViewer' : {
        method : 'post',
        func : _loginAsViewer
    }
}
