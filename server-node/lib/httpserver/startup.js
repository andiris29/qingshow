var express = require('express');
var fs = require('fs');
var cookieParser = require("cookie-parser");
var credentials = require("./credentials");
var connect = require('connect');
var sessionMongoose = require("session-mongoose");
var bodyParser = require('body-parser');
var path = require('path');
var _ = require('underscore');
var winston = require('winston');
var qsftp = require('../runtime').ftp;

//Services Name

var servicesNames = [
'feeding', 
'user', 
'show', 
'admin', 
'trade', 
'spread', 
'people', 
'matcher', 
'notify', 
'shop', 
'userBonus', 
'item', 
'dashboard', 
'goblin', 
'system'
];
var services = servicesNames.map(function (path) {
    return {
        'path' : path,
        'module' : require('./app/' + path)
    };
});

var wrapCallback = function (fullpath, callback) {
    return function (req, res) {
        res.qsPerformance = {
            'ip' : req.header('X-Real-IP') || req.connection.remoteAddress,
            'qsCurrentUserId' : req.qsCurrentUserId ? req.qsCurrentUserId.toString() : '',
            'fullpath' : fullpath,
            'start' : Date.now()
        };
        callback.func(req, res);
    };
};

var mkdirUploads = function (config) {
    for (var key in config) {
        var value = config[key];
        if (_.isObject(value)) {
            mkdirUploads(value);
        } else {
            if (value.indexOf('http://') !== 0) {
                qsftp.getConnection().mkdir(value, true, function (err){});
            }
        }
    }
};

module.exports = function (config, qsdb) {
    var app = express();
    global.qsConfig = config;
    // GZip
    app.use(connect.compress());
    
    app.listen(config.server.port);
    
    // Upload
    mkdirUploads(config.uploads);

    // Cross domain
    app.use(function (req, res, next) {
        // Set header for cross domain
        res.header('Access-Control-Allow-Credentials', true);
        res.header('Access-Control-Allow-Origin', req.headers.origin);
        res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE');
        res.header('Access-Control-Allow-Headers', 'X-Requested-With, X-HTTP-Method-Override, Content-Type, Accept');
        next();
    });

//Cookie
    app.use(cookieParser(credentials.cookieSecret));
//Session
    var SessionStore = sessionMongoose(connect);
    var session = require('express-session')({
        store : new SessionStore({
            interval : 24 * 60 * 60 * 1000,
            connection : qsdb.getConnection(),
            modelName : "sessionStores"
        }),
        cookie : {
            maxAge : 365 * 24 * 60 * 60 * 1000
        },
        resave : false,
        saveUninitialized : true,
        secret : credentials.sessionSecret
    });
    app.use(session);

    app.use(require('./middleware/queryStringParser'));
    app.use(bodyParser.json());
    app.use(bodyParser.urlencoded({
        extended : true
    }));
    app.use(require('./middleware/sessionParser'));
    app.use(require('./middleware/permissionValidator')(services));
    app.use(require('./middleware/errorHandler'));
// Regist http services
    services.forEach(function(service) {
        var module = service.module, path = service.path;
        for (var id in module) {
            var fullpath = '/services/' + path + '/' + id;
            var method = module[id].method, callback = module[id];
            if (method === 'get') {
                app.get(fullpath, wrapCallback(fullpath, callback));
            } else if (method === 'post') {
                app.post(fullpath, wrapCallback(fullpath, callback));
            }
        }
    });
    winston.info('Http server startup complete!');
};
