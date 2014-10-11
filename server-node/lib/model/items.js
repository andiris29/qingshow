var mongoose = require('mongoose');

var Schema = mongoose.Schema;
var itemSchema = Schema({
	category: Number,	//0 Tops 1 Bottoms 2 Shoes
	name: String,
	cover: String,
	source: String
});
var Item = mongoose.model('items', itemSchema);
module.exports = Item;