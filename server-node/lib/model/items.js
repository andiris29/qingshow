var mongoose = require('mongoose');
var Brand = require('./brands');

var Schema = mongoose.Schema;
var itemSchema;
itemSchema = Schema({
    category: Number,   //0 Tops 1 Bottoms 2 Shoes
    name: String,
    cover: String,
    brandRef: { type: Schema.Types.ObjectId, ref: 'brands'},
    source: String
});
var Item = mongoose.model('items', itemSchema);
module.exports = Item;