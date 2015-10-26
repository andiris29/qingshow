var async = require('async');
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
    global.qsConfig = config;
    // Initialize logger
    require('./runtime').loggers.init(config.logging, config.mongodb);

    //Database Connection
    var qsdb = require('./runtime').db;
    qsdb.connect(config.mongodb);

    // CDN connection
    var qsftp = require('./runtime').ftp;
    qsftp.connect(config.ftp, function () {
        try {
            // Startup http server
            require('./httpserver/startup')(config, qsdb);
        } catch (err) {
        }
        try {
            // Startup scheduled
            require('./scheduled/startup')(config.schedule);
        } catch (err) {
        }
        try {
            // Startup goblin overseer
            require('./goblin-overseer/startup')();
        } catch (err) {
        }
    });
});
