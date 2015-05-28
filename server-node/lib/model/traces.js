var mongoose = require('mongoose');

var Schema = mongoose.Schema;
var traceSchema;
traceSchema = Schema({
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

var Trace = mongoose.model('traces', traceSchema);

module.exports = Trace;

