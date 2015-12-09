var mongoose = require('mongoose');

var _db;
var connect = function(mongodbConfig) {
    var opts = {
        server : {
            poolSize : mongodbConfig.poolSize,
            socketOptions : {
                keepAlive : 1
            }
        },
        user : mongodbConfig.user,
        pass : mongodbConfig.password
    };
    connectStr = 'mongodb://' + mongodbConfig.url + ':' + mongodbConfig.port + '/' + mongodbConfig.schema;
    mongoose.connect(connectStr, opts);
};

var getConnection = function() {
    return mongoose.connection;
};

module.exports = {
    'connect' : connect,
    'getConnection' : getConnection
};

