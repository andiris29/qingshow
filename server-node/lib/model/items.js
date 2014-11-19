var mongoose = require('mongoose');
var Brand = require('./brands');
var Show = require('./shows');

var Schema = mongoose.Schema;
var itemSchema;
itemSchema = Schema({
    category: Number,   // <code>
    name: String,
    cover: String,
    brandRef: { type: Schema.Types.ObjectId, ref: 'brands'},
    source: String,
    showRefs: { type: Schema.Types.ObjectId, ref: 'shows'}
});
var Item = mongoose.model('items', itemSchema);
module.exports = Item;