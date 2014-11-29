var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var People = require('./peoples');

var RFollowPeople = mongoose.model('rFollowPeople', Schema({
    'peopleRef' : {
        'type' : Schema.Types.ObjectId,
        'ref' : 'peoples'
    },
    'followRef' : {
        'type' : Schema.Types.ObjectId,
        'ref' : 'peoples'
    },
    'create' : {
        'type' : Date,
        'default' : Date.now
    }
}, {
    collection : 'rFollowPeople'
}));

module.exports = RFollowPeople;
