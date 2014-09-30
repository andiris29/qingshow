var _db;

var connect = function(callback) {
    if (_db) {
        callback(null, _db);
    } else {
        var mongodb = require('mongodb');
        mongodb.MongoClient.connect('mongodb://127.0.0.1:' + 30002 + '/qingshow', function(err, db) {
            if (err) {
                callback(err);
            }
            var user = 'qingshow', password = 'qingshow@mongo';
            db.authenticate(user, password, function(err, res) {
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
