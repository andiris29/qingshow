var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var RPeopleFollowPeople = mongoose.model('rPeopleFollowPeople', Schema({
    'initiatorRef' : {
        'type' : Schema.Types.ObjectId,
        'ref' : 'peoples'
    },
    'targetRef' : {
        'type' : Schema.Types.ObjectId,
        'ref' : 'peoples'
    },
    'create' : {
        'type' : Date,
        'default' : Date.now
    }
}, {
    collection : 'rPeopleFollowPeople'
}));

module.exports = RPeopleFollowPeople;
