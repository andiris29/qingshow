var async = require('async'),
    uuid = require('node-uuid'),
    path = require('path'),
    fs = require('fs'),
    crypto = require('crypto'),
    request = require('request');

var JPushAudience = require('../../dbmodels').JPushAudience,
    People = require('../../dbmodels').People,
    PeopleCode = require('../../dbmodels').PeopleCode;

var qsftp = require('../../runtime').ftp;

var TraceHelper = require('../../helpers/TraceHelper'),
    RequestHelper = require('../../helpers/RequestHelper'),
    ResponseHelper = require('../../helpers/ResponseHelper'),
    SMSHelper = require('../../helpers/SMSHelper'),
    NotificationHelper = require('../../helpers/NotificationHelper'),
    ContextHelper = require('../../helpers/ContextHelper');

var VersionUtil = require('../../utils/VersionUtil');

var errors = require('../../errors');

var _secret = 'qingshow@secret';

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

var userPortraitResizeOptions = [
    {'suffix' : '_200', 'width' : 200, 'height' : 200},
    {'suffix' : '_100', 'width' : 100, 'height' : 100},
    {'suffix' : '_50', 'width' : 50, 'height' : 50},
    {'suffix' : '_30', 'width' : 30, 'height' : 30}
];

var WX_APPID = 'wx75cf44d922f47721',
    WX_SECRET = 'b2d418fcb94879affd36c8c3f37f1810';
    
var _addRegistrationId = function(peopleId, registrationId) {
    if (!registrationId || registrationId.length === 0) {
        return;
    }

    JPushAudience.remove({
        'registrationId' : registrationId
    }, function(err) {
        if (err) {
            return;
        }

        var info = new JPushAudience({
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

    JPushAudience.remove({
        'peopleRef' : peopleId,
        'registrationId' : registrationId
    }, function(err) {});
};

var _validateMobile = function(req, res, next) {
    SMSHelper.checkVerificationCode(req, req.body.mobile, req.body.verificationCode, function(err, success){
        if (!success || err) {
            next(err);
        } else {
            next();
        }
    });
};

var _injectWeixinUser = function(req, res, next) {
    async.waterfall([function(callback) {
        var token_url = 'https://api.weixin.qq.com/sns/oauth2/access_token?appid=' + WX_APPID + '&secret=' + WX_SECRET + '&code=' + req.body.code + '&grant_type=authorization_code';
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
    }], function(err, weixinUser) {
        if (err) {
            next(err);
        } else {
            req.injection.weixinUser = weixinUser;
            next();
        }
    });
};

var _saveWeixinUser = function(req, res, next) {
    var people = req.injection.qsCurrentUser,
        weixinUser = req.injection.weixinUser,
        copyHeadPath = null;
    
    async.waterfall([
        function(callback) {
            // Download portrait
            if (weixinUser.headimgurl && 
                (!people.userInfo.weixin || people.userInfo.weixin.headimgurl !== weixinUser.headimgurl)) {
                //download headIcon
                _downloadHeadIcon(weixinUser.headimgurl, function (err, tempPath) {
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
                                copyHeadPath = global.qsConfig.uploads.user.portrait.exposeToUrl + '/' + path.relative(global.qsConfig.uploads.user.portrait.ftpPath, newPath);
                                callback();
                            }
                            try {
                                fs.unlink(tempPath, function() {});
                            } catch (e) {
                            }
                        });
                    }
                });
            } else {
                callback(null);
            }
        },
        function(callback) {
            // Save weixinUser
            if (people.role === PeopleCode.ROLE_GUEST) {
                people.nickname = weixinUser.nickname;
            } else {
                people.nickname = people.nickname || weixinUser.nickname;
            }
            
            if (copyHeadPath && copyHeadPath.length) {
                people.portrait = copyHeadPath;
            }
            people.save(function(err, people) {
                People.update(
                    {'_id' : people._id},
                    {'$set' : {'userInfo.weixin' : weixinUser}},
                    function() {callback(err, people);}
                );
            });
        }
    ], function(err) {
        next(err);
    });
};

// ------------------
// Services
// ------------------
var user = {};

user.get = [
    require('../middleware/injectCurrentUser'),
    function(req, res, next) {
        if (req.injection.qsCurrentUser) {
            ContextHelper.appendPeopleContext(req.injection.qsCurrentUser._id, [req.injection.qsCurrentUser], function(err) {
                ResponseHelper.writeData(res, {
                    'people' : req.injection.qsCurrentUser
                });
                next();
            });
        } else {
            next();
        }
    }
];

user.login = [function(req, res, next) {
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
        "$and" : [{
            "$or" : [{"userInfo.id" : id}, 
                {"mobile" : id}]
        }, {
            "$or" : [{ "userInfo.password" : password}, 
                {"userInfo.encryptedPassword" : _encrypt(password)}]
            }
        ]
    }).exec(function(err, people) {
        if (err) {
            next(errors.genUnkownError(err));
        } else if (people) {
            //login succeed
            req.session.userId = people._id;

            ResponseHelper.write(res, 
                {'invalidateTime' : 3600000}, 
                {'people' : people});
            next();
        } else {
            //login fail
            delete req.session.userId;
            
            next(errors.ERR_INCORRECT_PASSWORD);
        }
    });
}];

user.logout = function(req, res) {
    var id = req.qsCurrentUserId;
    if (req.session.registrationId) {
        _removeRegistrationId(id, req.session.registrationId);
        delete req.session.registrationId;
    }
    delete req.session.userId;
    delete req.qsCurrentUserId;
    var retData = {
        metadata : {
            "result" : 0
        }
    };
    res.json(retData);
};

user.register = [
    require('../middleware/injectCurrentUser'),
    function(req, res, next) {
        if (!req.injection.qsCurrentUser) {
            _replaceCurrectUser(req, new People());
        }
        next();
    },
    function(req, res, next) {
        if (req.injection.qsCurrentUser.role !== 0) {
            next(errors.AlreadyLoggedIn);
        } else {
            next();
        }
    }, 
    _validateMobile,
    require('../middleware/injectModelGenerator').generateInjectOne(People, 'exsited', function(req) {
        return {
            '_id' : {'$ne' : req.qsCurrentUserId},
            '$or': [
                {'userInfo.id' : req.body.mobile}, 
                {'mobile': req.body.mobile}
            ]
        };
    }),
    function(req, res, next) {
        if (req.injection.exsited) {
            next(errors.ERR_MOBILE_ALREADY_REGISTERED);
        } else {
            next();
        }
    }, function(req, res, next) {
        var people = req.injection.qsCurrentUser;
        people.role = 1;
        people.mobile = req.body.mobile;
        people.userInfo = {
            'id' : req.body.mobile,
            'encryptedPassword' : _encrypt(req.body.password)
        };
        people.save(function(err, people) {
            if (!people || err) {
                next(errors.genUnkownError(err));
            } else {
                req.session.userId = people._id;

                ResponseHelper.writeData(res, {
                    'people' : people
                });
                next();
            }
        });
    }
];

user.bindMobile = [
    require('../middleware/injectCurrentUser'),
    _validateMobile,
    require('../middleware/injectModelGenerator').generateInjectOne(People, 'exsited', function(req) {
        return {
            '_id' : {'$ne' : req.qsCurrentUserId},
            '$or': [
                {'userInfo.id' : req.body.mobile}, 
                {'mobile': req.body.mobile}
            ]
        };
    }),
    function(req, res, next) {
        if (req.injection.exsited) {
            req.injection.outdated = req.injection.qsCurrentUser;
            _replaceCurrectUser(req, req.injection.exsited);
        }
        next();
    }, function(req, res, next) {
        var people = req.injection.qsCurrentUser,
            outdated = req.injection.outdated;
            
        people.role = 1;
        people.userInfo = people.userInfo || {};
        people.mobile = people.userInfo.id = req.body.mobile;
        // Copy weixin from outdated
        if (outdated && outdated.userInfo && outdated.userInfo.weixin) {
            people.userInfo.weixin = outdated.userInfo.weixin;
            outdated.userInfo.weixin = null;
            outdated.save(function(){});
        }
        
        people.save(function(err, people) {
            if (!people || err) {
                next(errors.genUnkownError(err));
            } else {
                ResponseHelper.writeData(res, {
                    'people' : people
                });
                next();
            }
        });
    }
];

user.update = function(req, res) {
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
            if (field === 'mobile') {
                continue;
            }
            people.set(field, qsParam[field]);
        }
        people.save(callback);
    }], function(err, people) {
        ResponseHelper.response(res, err, {
            'people' : people
        });
    });
};



user.updatePortrait = function(req, res) {
    _upload(req, res, global.qsConfig.uploads.user.portrait, 'portrait', userPortraitResizeOptions);
};

user.updateBackground = function(req, res) {
    _upload(req, res, global.qsConfig.uploads.user.background, 'background');
};

user.saveReceiver = function(req, res) {
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
        RequestHelper.parseFile(req, config.ftpPath, null, resizeOptions, function(err, fields, file) {
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

user.removeReceiver = function(req, res) {
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
};

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

user.loginViaWeixin = [
    require('../middleware/injectCurrentUser'),
    function(req, res, next) {
        if (!req.injection.qsCurrentUser) {
            _replaceCurrectUser(req, new People());
        }
        next();
    },
    _injectWeixinUser,
    require('../middleware/injectModelGenerator').generateInjectOne(People, 'exsited', function(req) {
        return {
            '_id' : {'$ne' : req.qsCurrentUserId},
            'userInfo.weixin.openid' : req.injection.weixinUser.openid
        };
    }),
    function(req, res, next) {
        if (req.injection.exsited) {
            _replaceCurrectUser(req, req.injection.exsited);
        }
        next();
    },
    _saveWeixinUser,
    function(req, res, next) {
        ResponseHelper.writeData(res, {
            'people' : req.injection.qsCurrentUser
        });
        next();
    }
];

user.bindWeixin = [
    require('../middleware/validateLogin'),
    require('../middleware/injectCurrentUser'),
    _injectWeixinUser,
    require('../middleware/injectModelGenerator').generateInjectOne(People, 'exsited', function(req) {
        return {
            '_id' : {'$ne' : req.qsCurrentUserId},
            'userInfo.weixin.openid' : req.injection.weixinUser.openid
        };
    }),
    function(req, res, next) {
        if (req.injection.exsited) {
            next(errors.ERR_WEIXIN_ALREADY_REGISTERED);
        } else {
            next();
        }
    },
    _saveWeixinUser,
    function(req, res, next) {
        ResponseHelper.writeData(res, {
            'people' : req.injection.qsCurrentUser
        });
        next();
    }
];


user.requestVerificationCode = function(req, res){
    var mobile = req.body.mobile;
    async.waterfall([function(callback){
        SMSHelper.createVerificationCode(req, mobile, function(err, code){
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
            SMSHelper.createVerificationCode(req, mobile, function() {});
        });
    }],function(error, code) {
        ResponseHelper.response(res, error, {
        });
    });
};

user.forgotPassword = [
    _validateMobile,
    function(req, res, next){
        People.count({
            'mobile' : req.body.mobile
        }).exec(function(err, count){
            count > 0 ? next() : next(errors.PeopleNotExist);
        })
    },
    function(req, res, next) {
        req.session.resetPassword = {
            'mobile' : req.body.mobile
        };
        next();
    }
];

user.resetPassword = function(req, res){
    var params = req.body,
        mobile = null;
        password = params.password;
        
    async.waterfall([function(callback) {
        if (!req.session.resetPassword) {
            callback(errors.genUnkownError());
        } else {
            mobile = req.session.resetPassword.mobile;
            delete req.session.resetPassword;
            callback();
        }
    }, function(callback){
        People.find({
            'mobile' : mobile
        }, function(err, peoples) {
            if (peoples.length > 1) {
                callback(errors.genUnkownError());
            }else {
                callback(null , peoples);
            }
        });
    }, function(people, callback) {
        People.findOneAndUpdate({
            'mobile' : mobile
        }, {
            $unset : {
                'userInfo.password' : -1
            },
            $set : {
                'userInfo.id' : mobile,
                'userInfo.encryptedPassword' : _encrypt(password)
            }
        }, {
        }, function(error, people) {
            if (error) {
                callback(errors.genUnkownError());
            } else {
                callback(null, people); 
            }
        });
    }],function(error, people) {
        ResponseHelper.response(res, error, {
            'people' : people
        });
    });
};


user.readNotification = function(req, res) {
    var params = req.body;
    var criteria = {};
    for (var element in params) {
        var key = 'extra.' + element;
        criteria[key] = params[element];
    }

    NotificationHelper.read([req.qsCurrentUserId], criteria, function(error) {
        ResponseHelper.response(res, error, {});
    });
};

user.loginAsGuest = function(req, res){
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
        people.role = PeopleCode.ROLE_GUEST;
        people.save(function(err, people){
            req.session.userId = people._id;
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
};

user.bindJPush = function(req, res){
    var params = req.body;
    var registrationId = params.registrationId;
    People.findOne({
        '_id': req.qsCurrentUserId
    }, function(err, people) {
        req.session.registrationId = registrationId;
        _addRegistrationId(people._id, registrationId);
        ResponseHelper.response(res, err, {
            'people' : people
        });
    });
};

user.loginAsViewer = [
    require('../middleware/injectModelGenerator').generateInjectOne(People, 'exsited', function(req) {
        return {'userInfo.id' : RequestHelper.getIp(req)};
    }),
    function(req, res, next) {
        if (!req.injection.exsited) {
            var people = new People({
                'role' : PeopleCode.ROLE_VIEWER,
                'userInfo.id' : RequestHelper.getIp(req),
                'invitorRef' : RequestHelper.parseId(req.body.invitorRef)
            });
            people.save(function(err, people) {
                req.injection.exsited = people;
                next(err);
            });
        } else {
            next();
        }
    },
    function(req, res, next) {
        _replaceCurrectUser(req, req.injection.exsited);
        ResponseHelper.writeData(res, {'people' : req.injection.exsited});
        next();
    }
];

var _replaceCurrectUser = function(req, people) {
    // Replace current user with db.people
    req.injection.qsCurrentUser = people;
    req.qsCurrentUserId = req.session.userId = people._id;
};

module.exports = {
    'get' : {
        method : 'get',
        func : user.get,
        permissionValidators : ['loginValidator']
    },
    'login' : {
        method : 'post',
        func : user.login
    },
    'logout' : {
        method : 'post',
        func : user.logout,
        permissionValidators : ['loginValidator']
    },
    'register' : {
        method : 'post',
        func : user.register
    },
    'update' : {
        method : 'post',
        func : user.update,
        permissionValidators : ['loginValidator']
    },
    'updatePortrait' : {
        method : 'post',
        func : user.updatePortrait,
        permissionValidators : ['loginValidator']
    },
    'updateBackground' : {
        method : 'post',
        func : user.updateBackground,
        permissionValidators : ['loginValidator']
    },
    'saveReceiver' : {
        method : 'post',
        func : user.saveReceiver,
        permissionValidators : ['loginValidator']
    },
    'removeReceiver' : {
        method : 'post',
        func : user.removeReceiver,
        permissionValidators : ['loginValidator']
    },
    'loginViaWeixin' : {
        method : 'post',
        func : user.loginViaWeixin
    },
    'bindWeixin' : {
        method : 'post',
        func : user.bindWeixin
    },
    'bindMobile' : {
        method : 'post',
        func : user.bindMobile
    },
    'requestVerificationCode' : {
        method : 'post',
        func : user.requestVerificationCode
    },
    'forgotPassword' : {
        method : 'post',
        func : user.forgotPassword
    },
    'resetPassword' : {
        method : 'post',
        func : user.resetPassword
    },
    'readNotification' : {
        method : 'post',
        permissionValidators : ['loginValidator'],
        func : user.readNotification
    },
    'loginAsGuest' : {
        method : 'post',
        func : user.loginAsGuest
    },
    'bindJPush' : {
        method : 'post',
        permissionValidators : ['loginValidator'],
        func : user.bindJPush
    },
    'loginAsViewer' : {
        method : 'post',
        func : user.loginAsViewer
    }
};
