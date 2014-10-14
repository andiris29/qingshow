var argv = require('minimist')(process.argv.slice(2));
var express = require('express');
var qsdb = require('../runtime/qsdb');
var connect = require('connect');
//param parser
var bodyParser = require('body-parser');
var queryStringParser = require('./middleware/query-string-parser');
//Cookie and session
var credentials = require("./credentials");
var cookieParser = require("cookie-parser");
var sessionMongoose = require("session-mongoose");

//user-validate
var userValidate = require('./middleware/user-validate');

//Database Connection
qsdb.connect();

//Services Name
var servicesNames = ['feeding', 'user'];

// Startup http server
var app = express();

app.listen(argv['http-server-port']);

//Cookie
app.use(cookieParser(credentials.cookieSecret));
//Session
var SessionStore = sessionMongoose(connect);
var store = new SessionStore({
    interval: 24 * 60 * 60 * 1000,
    connection: qsdb.getConnection(),
    modelName : "sessionStores"
});

var session = require('express-session')({
    store: store,
    cookie: { maxAge: 365 * 24 * 60 * 60 * 1000 },
    resave: true,
    saveUninitialized: true,
    secret: 'keyboard cat'
});

app.use(session);

//user validate
app.use(userValidate(servicesNames));

// Regist http services
_registServices = function (path) {
    var module = require('./services/' + path);
    for (var id in module) {
        var method = module[id].method, callback = module[id].func;
        if (method === 'get') {
            app.get('/services/' + path + '/' + id, callback);
        } else if (method === 'post') {
            app.post('/services/' + path + '/' + id, callback);
        }
    }
};

app.use(queryStringParser);
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
  extended: true
}));



servicesNames.forEach(function (name){
   _registServices(name);
});

console.log('Http server startup complete!');
