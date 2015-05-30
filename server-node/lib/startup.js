var argv = require('minimist')(process.argv.slice(2));

var async = require('async');
var fs = require('fs');
var path = require('path');

var qsdb = require('./runtime/qsdb');

// Log
var folderLogs = path.join(__dirname, 'logs');
if (!fs.existsSync(folderLogs)) {
    fs.mkdirSync(folderLogs);
}
var winston = require('winston');
winston.add(winston.transports.DailyRotateFile, {
    'filename' : path.join(folderLogs, 'winston.log')
});

//Database Connection
qsdb.connect();

// Startup http server
var uploadsCfg = argv['uploads'].split(',');
var folderUploads = uploadsCfg[0], pathUploads = uploadsCfg[1];
require('./httpserver/startup')(argv['app-server-port'], folderUploads, pathUploads, qsdb);

// Startup scheduled
require('./scheduled/startup')();

// Handle uncaught exceptions
process.on('uncaughtException', function(err) {
    winston.error(new Date().toString() + ': uncaughtException');
    winston.error(err);
    winston.error('\t' + err.stack);
});
