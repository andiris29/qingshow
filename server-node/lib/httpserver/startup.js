var argv = require('minimist')(process.argv.slice(2));
var express = require('express');
var qsdb = require('../runtime/qsdb');
var connect = require('connect');
var path = require('path');
var fs = require('fs');

//param parser
var bodyParser = require('body-parser');
var queryStringParser = require('./middleware/query-string-parser');
//Cookie and session
var credentials = require("./credentials");
var cookieParser = require("cookie-parser");
var sessionMongoose = require("session-mongoose");

//Database Connection
qsdb.connect();

//Services Name
var servicesNames = ['feeding', 'user', 'query', 'potential', 'people', 'brand', 'show'];
var services = servicesNames.map(function(path) {
    return {
        'path' : path,
        'module' : require('./services/' + path)
    };
});

// Startup http server
var app = express();
app.listen(argv['http-server-port']);

// Upload
var uploadsCfg = argv['uploads'].split(',');
var folderUploads = uploadsCfg[0], pathUploads = uploadsCfg[1];
if (!fs.existsSync(folderUploads)) {
    fs.mkdirSync(folderUploads);
}
global.__qingshow_uploads = {
    'folder' : folderUploads,
    'path' : pathUploads,
};
app.use(pathUploads, express.static(folderUploads));
//cross domain
app.use(function(req, res, next) {
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
    resave : true,
    saveUninitialized : true,
    secret : credentials.sessionSecret
});
app.use(session);

app.use(queryStringParser);
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
    extended : true
}));
//user validate
var userValidate = require('./middleware/user-validate');
app.use(userValidate(servicesNames));

// app.use(require('./middleware/paramParser')(services));

var error_handler = require('./middleware/error-handler');
app.use(error_handler);

// Regist http services
services.forEach(function(service) {
    var module = service.module, path = service.path;
    for (var id in module) {
        var method = module[id].method, callback = module[id].func;
        if (method === 'get') {
            app.get('/services/' + path + '/' + id, callback);
        } else if (method === 'post') {
            app.post('/services/' + path + '/' + id, callback);
        }
    }
});

console.log('Http server startup complete!');
