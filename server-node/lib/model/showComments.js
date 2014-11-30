var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var schema = Schema({
    'targetRef' : {
        'type' : Schema.Types.ObjectId,
        'ref' : 'shows'
    },
    'authorRef' : {
        'type' : Schema.Types.ObjectId,
        'ref' : 'peoples'
    },
    'atRef' : {
        'type' : Schema.Types.ObjectId,
        'ref' : 'peoples'
    },
    'comment' : String,
    'create' : {
        'type' : Date,
        'default' : Date.now
    },
    'delete' : {
        'type' : Date
    }
}, {
    'collection' : 'showComments'
});
var ShowComment = mongoose.model('showComments', schema);
module.exports = ShowComment;
