var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var rPeopleCreateTrade = mongoose.model('rPeopleCreateTrade', Schema({
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
    collection : 'rPeopleCreateTrade'
}));

module.exports = rPeopleCreateTrade;

