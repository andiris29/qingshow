var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var RPeopleViewShow = mongoose.model('rPeopleViewShow', Schema({
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
    collection : 'rPeopleViewShow'
}));

module.exports = RPeopleViewShow;
