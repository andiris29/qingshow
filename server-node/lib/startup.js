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
    
    // Load additional configs
    properties.parse(
        path.join(__dirname, 'modelRemixConfig.properties'), 
        {'path' : true, 'namespaces' : true, 'variables' : true}, 
        function(err, config) {
            global.qsConfig.modelRemix = config;
        });
    properties.parse(
        path.join(__dirname, 'itemRemixConfig.properties'), 
        {'path' : true, 'namespaces' : true, 'variables' : true}, 
        function(err, config) {
            global.qsConfig.itemRemix = config;
        });
        
    // Initialize logger
    require('./runtime').loggers.init();

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
            console.log(err);
        }
        try {
            // Startup scheduled
            require('./scheduled/startup')(config.schedule);
        } catch (err) {
            console.log(err);
        }
    });
});

