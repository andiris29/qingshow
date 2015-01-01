var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var RPeopleLikePreview = mongoose.model('rPeopleLikePreview', Schema({
    'initiatorRef' : {
        'type' : Schema.Types.ObjectId,
        'ref' : 'peoples'
    },
    'targetRef' : {
        'type' : Schema.Types.ObjectId,
        'ref' : 'previews'
    },
    'create' : {
        'type' : Date,
        'default' : Date.now
    }
}, {
    collection : 'rPeopleLikePreview'
}));

module.exports = RPeopleLikePreview;
