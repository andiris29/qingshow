var mongoose = require('mongoose');
var Show = require('./shows');
var People = require('./peoples');

var Schema = mongoose.Schema;
var commentSchema;
commentSchema = Schema({
    showRef: { type: Schema.Types.ObjectId, ref: 'shows'},
    peopleRef: { type: Schema.Types.ObjectId, ref: 'peoples'},
    comment: String,
    create: { type: Date, 'default': Date.now }
});
var Comment = mongoose.model('comments', commentSchema);
module.exports = Comment;