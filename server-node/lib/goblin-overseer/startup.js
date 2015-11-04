var path = require('path');
var properties = require("properties");
var request = require('request'),
    winston = require('winston');

var GoblinSlave = require('./GoblinSlave');

var configPath = path.join(__dirname, 'config.properties');

var _config;

module.exports = function () {
    properties.parse(configPath, {
        path : true,
        namespaces : true,
        variables : true
    }, function(error, config) {
        _config = config;
        GoblinSlave.start(_config);
    });
    
    winston.info('Startup goblin-overseer success');
};

// Handle uncaught exceptions
process.on('uncaughtException', function(err) {
    GoblinSlave.continue();

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

