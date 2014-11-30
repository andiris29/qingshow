var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var RPeopleFollowBrand = mongoose.model('rPeopleFollowBrand', Schema({
    'initiatorRef' : {
        'type' : Schema.Types.ObjectId,
        'ref' : 'peoples'
    },
    'targetRef' : {
        'type' : Schema.Types.ObjectId,
        'ref' : 'brands'
    },
    'create' : {
        'type' : Date,
        'default' : Date.now
    }
}, {
    collection : 'rPeopleFollowBrand'
}));

module.exports = RPeopleFollowBrand;
