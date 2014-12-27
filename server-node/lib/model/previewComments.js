var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var schema = Schema({
    'targetRef' : {
        'type' : Schema.Types.ObjectId,
        'ref' : 'previews'
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
    'collection' : 'previewComments'
});
var PreviewComment = mongoose.model('previewComments', schema);
module.exports = PreviewComment;
