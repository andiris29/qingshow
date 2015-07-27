var argv = require('minimist')(process.argv.slice(2));

var async = require('async');
var fs = require('fs');
var path = require('path');
var properties = require ("properties");

// Log
var folderLogs = path.join(__dirname, 'logs');
if (!fs.existsSync(folderLogs)) {
    fs.mkdirSync(folderLogs);
}
var winston = require('winston');
winston.add(winston.transports.DailyRotateFile, {
    'filename' : path.join(folderLogs, 'winston.log')
});

// Load the config file(config.properties)
var configPath = path.join(__dirname, 'config.properties');
properties.parse(configPath, {
    path : true,
    namespaces : true,
    variables : true
}, function(error, config) {
    if (error) {
        console.error (error);
        return;
    }
    // Load handle
    winston.info(config);

    //Database Connection
    var qsdb = require('./runtime/qsdb');
    qsdb.connect(config.mongodb);



    var qsftp = require('./runtime/qsftp');
    qsftp.connect(config.ftp, function () {
        // Startup http server
        require('./httpserver/startup')(config, qsdb);

        // Startup scheduled
        require('./scheduled/startup')();
    });


});

// Handle uncaught exceptions
process.on('uncaughtException', function(err) {
    winston.error(new Date().toString() + ': uncaughtException');
    winston.error(err);
    winston.error('\t' + err.stack);
});
