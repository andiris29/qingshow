var mongoose = require('mongoose');

var _db;
var connect = function(config) {
    var mongodbConfig = config.mongodb;
    var opts = {
        server : {
            socketOptions : {
                keepAlive : 1
            }
        },
        user : mongodbConfig.user,
        pass : mongodbConfig.password
    };
    connectStr = 'mongodb://' + mongodbConfig.url + ':' + mongodbConfig.port + '/' + mongodbConfig.schema;
    mongoose.connect(connectStr, opts);

    require('./qsmail').debug('Startup', JSON.stringify(config, null, 4), function(err, info) {
    });
};
var getConnection = function() {
    return mongoose.connection;
};
module.exports = {
    'connect' : connect,
    'getConnection' : getConnection
};

