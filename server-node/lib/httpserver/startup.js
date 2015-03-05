var argv = require('minimist')(process.argv.slice(2));

var qsdb = require('../runtime/qsdb');
var async = require('async');

// Log
var winston = require('winston');
winston.add(winston.transports.DailyRotateFile, {
    'filename' : require('path').join(__dirname, 'winston.log')
});

//Database Connection
qsdb.connect();

//Services Name
var servicesNames = ['feeding', 'itemFeeding', 'user', 'potential', 'people', 'brand', 'show', 'preview', 'admin', 'goblin'];
var services = servicesNames.map(function(path) {
    return {
        'path' : path,
        'module' : require('./services/' + path)
    };
});

// Startup http server
var uploadsCfg = argv['uploads'].split(',');
var folderUploads = uploadsCfg[0], pathUploads = uploadsCfg[1];

var serviceStartUp = require('./services/startup');
serviceStartUp.start(argv['app-server-port'], folderUploads, pathUploads, qsdb);


// Handle uncaught exceptions
process.on('uncaughtException', function(err) {
    console.log(new Date().toString() + ': uncaughtException');
    console.log(err);
    console.log('\t' + err.stack);
});
