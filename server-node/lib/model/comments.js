var mongoose = require('mongoose');

var Schema = mongoose.Schema;
var commentSchema = Schema({
	showRef: { type: Schema.Types.ObjectId, ref: 'shows'},
	peopleRef: { type: Schema.Types.ObjectId, ref: 'peoples'},
	comment: String,
	time: { type: Date, default: Date.now }
});
var Comment = mongoose.model('comments', commentSchema);
module.exports = Comment;