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
var TraceHelper = require('../helpers/TraceHelper'),
    RequestHelper = require('../helpers/RequestHelper'),
    ResponseHelper = require('../helpers/ResponseHelper');
//Services Name

var servicesNames = [
    'feeding', 
    'feedingAggregation',
    'user', 
    'bonus',
    'show', 
    'admin', 
    'trade',  
    'people', 
    'matcher', 
    'notify', 
    'shop', 
    'item', 
    'dashboard', 
    'goblin', 
    'system',
    'share',
    'trace'
];
var services = servicesNames.map(function (path) {
    return {
        'path' : path,
        'module' : require('./app/' + path)
    };
});

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
        res.header('Access-Control-Allow-Headers', [
            'X-Requested-With', 
            'X-HTTP-Method-Override', 
            'Content-Type', 
            'Accept',
            'qs-version', 
            'qs-version-code', 
            'qs-type', 
            'qs-device-uid', 
            'qs-device-model', 
            'qs-os-type', 
            'qs-os-version']);
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
        saveUninitialized : false,
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
    app.use(_errMiddleware);
    
    // Register http services
    services.forEach(function(service) {
        var module = service.module, path = service.path;
        for (var id in module) {
            var fullpath = '/services/' + path + '/' + id;
            var method = module[id].method, func = module[id].func;
            
            var middlewares = [_injectionMiddleware,
                _genLogMiddleware(fullpath)];
            if (_.isArray(func)) {
                middlewares = middlewares.concat(func);
                func = function(req, res) {
                    res.locals.out = res.locals.out || {};
                    ResponseHelper.response(res, res.locals.out.err, res.locals.out.data, res.locals.out.metadata);
                };
            }
            middlewares.push(_errMiddleware);
            
            app[method](fullpath, middlewares, func);
        }
    });
    winston.info('Http server startup complete!');
};

var _errMiddleware = function (err, req, res, next) {
    if (!err) {
        next();
    } else {
        res.locals.out = res.locals.out || {};
        ResponseHelper.response(res, err, res.locals.out.data, res.locals.out.metadata);
    }
};

var _injectionMiddleware = function (req, res, next) {
    req.injection = req.injection || {};
    next();
};

var _genLogMiddleware = function(fullpath) {
    return function (req, res, next) {
        res.locals = res.locals || {};
        res.locals.api = {
            'fullpath' : fullpath,
            'timestamp' : Date.now(),
            'req' : {
                'client' : RequestHelper.getClientInfo(req),
                'qsCurrentUserId' : req.qsCurrentUserId ? req.qsCurrentUserId.toString() : '',
                'queryString' : req.queryString,
                'body' : req.body
            }
        };
        
        TraceHelper.trace('api-request', req, res.locals.api);
        next();
    };
};
