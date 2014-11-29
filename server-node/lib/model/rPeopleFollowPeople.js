var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var People = require('./peoples');

var RPeopleFollowPeople = mongoose.model('rPeopleFollowPeople', Schema({
    'initiatorRef' : {
        'type' : Schema.Types.ObjectId,
        'ref' : 'peoples'
    },
    'affectedRef' : {
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
