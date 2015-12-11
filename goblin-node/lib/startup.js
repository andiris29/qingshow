var winston = require('winston');
var moment = require('moment');
var path = require('path');
var properties = require("properties");

var configPath = path.join(__dirname, 'config.properties');

var _config;

winston.add(winston.transports.DailyRotateFile, {
    'filename' : path.join(__dirname, 'winston.info'),
    'timestamp' : function(){
        return moment()._d.toString();
    }
});
    
properties.parse(configPath, {
    path : true,
    namespaces : true,
    variables : true
}, function(error, config) {
    global.qsConfig = _config = config;

    var GoblinSlave = require('./goblin-overseer/GoblinSlave');
    GoblinSlave.start(_config);
});

winston.info('Startup goblin-overseer success');

// Handle uncaught exceptions
process.on('uncaughtException', function(err) {
    winston.error(new Date().toString() + ': uncaughtException');
    winston.error(err);
    winston.error('\t' + err.stack);

    var GoblinSlave = require('./goblin-overseer/GoblinSlave');
    GoblinSlave.continue();

    var path = _config.server.path + '/services/system/log';
    var param = {
        client : 'goblin-overseer',
        level : 'error',
        message : 'uncaughtException:' + err,
        stack : err.stack
    };
    var request = require('request');if (global.qsConfig && global.qsConfig.proxy) {request = request.defaults({'proxy' : global.qsConfig.proxy});}
    request.post({
        url: path,
        form: param
    }, function(err, httpResponse, body){
    });
});

