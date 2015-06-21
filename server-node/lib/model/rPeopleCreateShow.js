var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var rPeopleCreateShow = mongoose.model('rPeopleCreateShow', Schema({
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
    collection : 'rPeopleCreateShow'
}));

module.exports = rPeopleCreateShow;

