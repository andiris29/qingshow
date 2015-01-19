var mongoose = require('mongoose');

var argv = require('minimist')(process.argv.slice(2));
var connectCfg = argv['mongodb-connect'].split(',');
var authCfg = argv['mongodb-auth'].split(',');

var _db;
var connect = function() {
    var opts = {
        server : {
            socketOptions : {
                keepAlive : 1
            }
        },
        user : authCfg[0],
        pass : authCfg[1]
    };
    connectStr = 'mongodb://' + connectCfg[0] + ':' + connectCfg[1] + '/' + connectCfg[2];
    mongoose.connect(connectStr, opts);

    require('./qsmail').debug('Startup', JSON.stringify(argv, null, 4), function(err, info) {
    });
};
var getConnection = function() {
    return mongoose.connection;
};
module.exports = {
    'connect' : connect,
    'getConnection' : getConnection
};
