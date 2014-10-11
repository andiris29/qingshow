var argv = require('minimist')(process.argv.slice(2));
// Startup http server
var express = require('express');
var qsdb = require('../runtime/qsdb');
var bodyParser = require('body-parser');

var app = express();

app.listen(argv['http-server-port']);

// Regist http services
_registServices = function(path) {
    var module = require('./services/' + path);
    for (var id in module) {
        var method = module[id][0], callback = module[id][1];
        if (method === 'get') {
            app.get('/services/' + path + '/' + id, callback);
        } else if (method === 'post') {
            app.post('/services/' + path + '/' + id, callback);
        }
    }
};

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
  extended: true
}));

_registServices('feeding');

qsdb.mongooseConnect();
console.log('Http server startup complete!');
