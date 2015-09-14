
var GoblinSlaver = require('./scheduled/goblin/slaver/GoblinSlaver');
var path = require('path');
var properties = require("properties");


var configPath = path.join(__dirname, 'config.properties');


properties.parse(configPath, {
    path : true,
    namespaces : true,
    variables : true
}, function(error, config) {
    if (!error) {
        GoblinSlaver.start(config);
    }
});

// Handle uncaught exceptions
process.on('uncaughtException', function(err) {
    GoblinSlaver.continue();

    console.log(new Date().toString() + ': uncaughtException');
    console.log(err);
    console.log('\t' + err.stack);
});

