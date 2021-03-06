var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var RPeopleLikeShow = mongoose.model('rPeopleLikeShow', Schema({
    'initiatorRef' : {
        'type' : Schema.Types.ObjectId,
        'ref' : 'peoples'
    },
    'targetRef' : {
        'type' : Schema.Types.ObjectId,
        'ref' : 'shows'
    },
    'create' : {
        'type' : Date,
        'default' : Date.now
    }
}, {
    collection : 'rPeopleLikeShow'
}));

module.exports = RPeopleLikeShow;
