var argv = require('minimist')(process.argv.slice(2));
// Startup http server
var express = require('express');
var app = express();
app.listen(argv['http-server-port']);

// Regist http services
_registServices = function(path) {
    var module = require('./services/' + path);
    for (var id in module) {
        var method = module[id][0], callback = module[id][1];
        if (method === 'get') {
            app.get(path, callback);
        } else if (method === 'post') {
            app.post(path, callback);
        }
    }
};
_registServices('feeding');

console.log('Http server startup complete!');
