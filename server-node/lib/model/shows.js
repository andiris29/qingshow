
var mongoose = require('mongoose');

var Schema = mongoose.Schema;
var showSchema = Schema({
	cover: String,
	video: String,
	posters: [String],
	numLike: Number,
	producerRef: {type: Schema.Types.ObjectId, ref: 'peoples'},
	itemRef: [{type: Schema.Types.ObjectId, ref: 'items'}],
	numView: Number,
	tags: [String]
});

var Show = mongoose.model('shows', showSchema);
module.exports = Show;