var async = require('async');
var fs = require('fs');
var path = require('path');
var properties = require("properties");


// ------------------
// Load & parse config.properties
// ------------------
var configPath = path.join(__dirname, 'config.properties');
properties.parse(configPath, {
    path : true,
    namespaces : true,
    variables : true
}, function(err, config) {
    if (err) {
        console.error(err);
        return;
    }
    // Initialize logger
    _initalizeLog(config.logging.dir);

    //Database Connection
    var qsdb = require('./runtime').db;
    qsdb.connect(config.mongodb);

    // CDN connection
    var qsftp = require('./runtime').ftp;
    qsftp.connect(config.ftp, function () {
        // Startup http server
        require('./httpserver/startup')(config, qsdb);
        // Startup scheduled
        require('./scheduled/startup')(config.schedule);
    });
});

var _initalizeLog = function(dir) {
    if (!fs.existsSync(dir)) {
        fs.mkdirSync(dir);
    }
    
    var winston = require('winston');
    // Default logger
    winston.add(winston.transports.DailyRotateFile, {
        'filename' : path.join(dir, 'winston.log')
    });
    winston.remove(winston.transports.Console);
    
    // Exception logger
    new winston.Logger({
        'exceptionHandlers' : [
            new winston.transports.DailyRotateFile({
                'filename' : path.join(dir, 'winston-exception.log')
            })
        ],
        'exitOnError' : false
    });
    
    // Performance logger
    winston.loggers.add('api', {
        'transports' : [
            new winston.transports.DailyRotateFile({
                'filename' : path.join(dir, 'winston-api.log')
            })
        ]
    });
    
    // Goblin logger
    winston.loggers.add('goblin', {
        'transports' : [
            new winston.transports.DailyRotateFile({
                'filename' : path.join(dir, 'winston-goblin.log')
            })
        ]
    });
    
    // Client logger
    winston.loggers.add('client', {
        'transports' : [
            new winston.transports.DailyRotateFile({
                'filename' : path.join(dir, 'winston-client.log')
            })
        ]
    });
};

