
var GoblinSlaver = require('./scheduled/goblin/slaver/GoblinSlaver');
var path = require('path');
var properties = require("properties");
var request = require('request');

var configPath = path.join(__dirname, 'config.properties');


properties.parse(configPath, {
    path : true,
    namespaces : true,
    variables : true
}, function(error, config) {
    if (!error) {
        GoblinSlaver.start(config);
    }
    global.config = config;
});



// Handle uncaught exceptions
process.on('uncaughtException', function(err) {
    GoblinSlaver.continue();

    var path = global.config.server.path + '/services/goblin/crawlItemFailed';
    var logStr = new Date().toString() + ': uncaughtException\n' + err + '\n'+ '\t' + err.stack;
    var param = {log : logStr};
    request.post({
        url: path,
        form: param
    }, function(err, httpResponse, body){
    });

    console.log(new Date().toString() + ': uncaughtException');
    console.log(err);
    console.log('\t' + err.stack);
});

