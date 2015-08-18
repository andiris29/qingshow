var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var RPeopleShareTrade = mongoose.model('rPeopleShareTrade', Schema({
    'initiatorRef' : {
        'type' : Schema.Types.ObjectId,
        'ref' : 'peoples'
    },
    'targetRef' : {
        'type' : Schema.Types.ObjectId,
        'ref' : 'trades'
    },
    'create' : {
        'type' : Date,
        'default' : Date.now
    }
}, {
    collection : 'rPeopleShareTrade'
}));

module.exports = RPeopleShareTrade;
