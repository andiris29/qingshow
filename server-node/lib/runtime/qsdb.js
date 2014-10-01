var argv = require('minimist')(process.argv.slice(2));
var connectCfg = argv['mongodb-connect'].split(',');
var authCfg = argv['mongodb-auth'].split(',');

var _db;
var connect = function(callback) {
    if (_db) {
        callback(null, _db);
    } else {
        var mongodb = require('mongodb');
        mongodb.MongoClient.connect('mongodb://' + connectCfg[0] + ':' + connectCfg[1] + '/' + connectCfg[2], function(err, db) {
            if (err) {
                callback(err);
            }
            db.authenticate(authCfg[0], authCfg[1], function(err, res) {
                if (err) {
                    callback(err);
                } else {
                    _db = db;
                    callback(null, db);
                }
            });
        });
    }
};

module.exports = {
    'connect' : connect
};
