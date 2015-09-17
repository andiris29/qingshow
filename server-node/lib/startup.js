var async = require('async');
var fs = require('fs');
var path = require('path');
var properties = require("properties");

// ------------------
// Initialize logger
// ------------------
var folderLogs = path.join(__dirname, 'logs');
if (!fs.existsSync(folderLogs)) {
    fs.mkdirSync(folderLogs);
}

var winston = require('winston');
// Default logger
winston.add(winston.transports.DailyRotateFile, {
    'filename' : path.join(folderLogs, 'winston.log')
});
winston.remove(winston.transports.Console);

// Exception logger
new winston.Logger({
    'exceptionHandlers' : [
        new winston.transports.DailyRotateFile({
            'filename' : path.join(folderLogs, 'winston-exception.log')
        })
    ],
    'exitOnError' : false
});

// Performance logger
winston.loggers.add('api', {
    'transports' : [
        new winston.transports.DailyRotateFile({
            'filename' : path.join(folderLogs, 'winston-api.log')
        })
    ]
});

// Goblin logger
winston.loggers.add('goblin', {
    'transports' : [
        new winston.transports.DailyRotateFile({
            'filename' : path.join(folderLogs, 'winston-goblin.log')
        })
    ]
});

// ------------------
// Load & parse config.properties
// ------------------
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
        require('./scheduled/startup')(config.schedule);
    });


});

