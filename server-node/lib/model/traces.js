var mongoose = require('mongoose');

var Schema = mongoose.Schema;
var traceSchema;
traceSchema = Schema({
    ip : String,
    version : String,
    deviceUid : String,
    osType : Number,
    osVersion : String,
    behavior : String,
    create : {
        type : Date,
        'default' : Date.now
    }
});

var Trace = mongoose.model('traces', traceSchema);

module.exports = Trace;

