
var mongoose = require('mongoose');

var Schema = mongoose.Schema;
var showSchema = Schema({
	cover: String,
	video: String,
	posters: [String],
	numLike:Number,
	producerRef:Schema.Types.ObjectId,
	itemRef:[Schema.Types.ObjectId],
	numView:Number,
	tags:[String]
});

var Show = mongoose.model('Show', showSchema);
module.exports = Show;