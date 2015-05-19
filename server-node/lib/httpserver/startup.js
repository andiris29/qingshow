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


//Services Name
var servicesNames = ['feeding', 'user', 'show', 'preview', 'admin', 'trade', 'chosen', 'item', 'itemfeeding'];
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


module.exports = function (appServerPort, folderUploads, pathUploads, qsdb) {
    var app = express();
    app.listen(appServerPort);

// Upload
    if (!fs.existsSync(folderUploads)) {
        fs.mkdirSync(folderUploads);
    }
    var files = fs.readdirSync(folderUploads);
    files.forEach(function (file) {
        if (file.indexOf('.lock') !== -1) {
            process.exit();
        }
    });

    global.__qingshow_uploads = {
        'folder' : folderUploads,
        'path' : pathUploads,
    };
//cross domain
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
