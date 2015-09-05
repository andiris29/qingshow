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
var qsftp = require('../runtime/qsftp');

//Services Name
var servicesNames = ['feeding', 'user', 'show', 'admin', 'trade', 'spread', 'people', 'matcher', 'notify', 'shop', 'userBonus', 'item', 'dashboard'];
var services = servicesNames.map(function (path) {
    return {
        'path' : path,
        'module' : require('./services/' + path)
    };
});

var wrapCallback = function (fullpath, callback) {
    return function (req, res) {
        res.qsPerformance = {
            'fullpath' : fullpath,
            'start' : Date.now()
        };
        var f = require('path').join(__dirname, 'performance.js');
        if (req.queryString && req.queryString.qsPerformance) {
            fs.appendFileSync(f, '// ' + new Date());
        }
        if (fs.existsSync(f)) {
            if (req.queryString && req.queryString.qsPerformance === 'unlink') {
                fs.unlinkSync(f);
            } else {
                res.qsPerformance.d = _.random(3000, 10000);
            }
        }
        // req.queryString.version: "1.2.0"
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
