var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var RPeopleShareShow = mongoose.model('rPeopleShareShow', Schema({
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
    collection : 'rPeopleShareShow'
}));

module.exports = RPeopleShareShow;
