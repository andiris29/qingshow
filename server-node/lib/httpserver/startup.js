var argv = require('minimist')(process.argv.slice(2));
var express = require('express');
var qsdb = require('../runtime/qsdb');
var connect = require('connect');
var path = require('path');
//param parser
var bodyParser = require('body-parser');
var queryStringParser = require('./middleware/query-string-parser');
//Cookie and session
var credentials = require("./credentials");
var cookieParser = require("cookie-parser");
var sessionMongoose = require("session-mongoose");

//user-validate
var userValidate = require('./middleware/user-validate');
//error-handler
var error_handler = require('./middleware/error-handler');

//Database Connection
qsdb.connect();

//Services Name
var servicesNames = ['feeding', 'user', 'interaction', 'query'];

// Startup http server
var app = express();
app.listen(argv['http-server-port']);

//static
var publicPath = path.join(__dirname, '../../public');
app.use(express.static(publicPath));

//cross domain
app.use(function (req, res, next) {
    // Set header for cross domain
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "X-Requested-With");
});

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

app.use(error_handler);

servicesNames.forEach(function (name){
   _registServices(name);
});

console.log('Http server startup complete!');
