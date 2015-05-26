var mongoose = require('mongoose');

var Schema = mongoose.Schema;
var logSchema;
logSchema = Schema({
    ip : String,
    version : Number,
    deviceUid : Number,
    osType : Number,
    osVersion : Number,
    behavior : String,
    behaviorInfo : {
        firstLaunch : {
            channel : String
        }
    },
    create : {
        type : Date,
        'default' : Date.now
    }
});

var Log = mongoose.model('logs', logSchema);

module.exports = Log;

