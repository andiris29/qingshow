var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var RPeopleLikeItem = mongoose.model('rPeopleLikeItem', Schema({
    'initiatorRef' : {
        'type' : Schema.Types.ObjectId,
        'ref' : 'peoples'
    },
    'targetRef' : {
        'type' : Schema.Types.ObjectId,
        'ref' : 'items'
    },
    'create' : {
        'type' : Date,
        'default' : Date.now
    }
}, {
    collection : 'rPeopleLikeItem'
}));

module.exports = RPeopleLikeItem;
