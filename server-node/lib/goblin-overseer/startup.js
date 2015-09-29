
var GoblinSlaver = require('./GoblinSlaver');
var path = require('path');
var properties = require("properties");
var request = require('request');

var configPath = path.join(__dirname, 'config.properties');

var _config;

module.exports = function () {
    properties.parse(configPath, {
        path : true,
        namespaces : true,
        variables : true
    }, function(error, config) {
        _config = config;
        GoblinSlaver.start(_config);
    });
};

// Handle uncaught exceptions
process.on('uncaughtException', function(err) {
    GoblinSlaver.continue();

    var path = _config.server.path + '/services/system/log';
    var param = {
        client : 'goblin-slaver',
        level : 'error',
        message : 'uncaughtException:' + err,
        stack : err.stack
    };
    request.post({
        url: path,
        form: param
    }, function(err, httpResponse, body){
    });

    console.log(new Date().toString() + ': uncaughtException');
    console.log(err);
    console.log('\t' + err.stack);
});

